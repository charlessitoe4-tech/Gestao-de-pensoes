package service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import dao.HibernateUtilizadorDAO;
import dao.UtilizadorDAO;
import model.Utilizador;

/** Recuperação de senha mediante um código temporário enviado para o email da conta. */
public final class RecuperacaoSenhaService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration VALIDADE_CODIGO = Duration.ofMinutes(10);
    private static final int MAX_TENTATIVAS = 5;

    private final UtilizadorDAO repositorio;
    private final UtilizadorService utilizadorService;
    private final EmailSender emailSender;
    private final ConcurrentMap<String, CodigoRecuperacao> codigos = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> ultimosEnvios = new ConcurrentHashMap<>();

    public RecuperacaoSenhaService() {
        this(new HibernateUtilizadorDAO(), new UtilizadorService(), new SmtpEmailSender());
    }

    public RecuperacaoSenhaService(UtilizadorDAO repositorio,
            UtilizadorService utilizadorService, EmailSender emailSender) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
        this.utilizadorService = Objects.requireNonNull(
                utilizadorService, "O serviço de utilizadores é obrigatório.");
        this.emailSender = Objects.requireNonNull(emailSender, "O serviço de email é obrigatório.");
    }

    /** Não indica à interface se o username existe, evitando enumeração de contas. */
    public synchronized void solicitarCodigo(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        String chave = normalizarUsername(username);
        Utilizador utilizador = repositorio.buscarPorUsername(username.trim()).orElse(null);
        if (utilizador == null || !utilizador.isAtivo()
                || !ValidacaoDadosMoçambique.emailValido(utilizador.getEmail())) {
            return;
        }
        Instant envioAnterior = ultimosEnvios.get(chave);
        if (envioAnterior != null && Instant.now().isBefore(envioAnterior.plusSeconds(60))) {
            throw new IllegalStateException(
                    "Aguarde um minuto antes de solicitar outro código de recuperação.");
        }

        String codigo = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        emailSender.enviar(utilizador.getEmail(), "Recuperação da senha",
                "O seu código de recuperação é: " + codigo
                + "\nO código expira em 10 minutos. Se não solicitou a recuperação, ignore este email.");
        Instant agora = Instant.now();
        ultimosEnvios.put(chave, agora);
        codigos.put(chave, new CodigoRecuperacao(codigo, agora.plus(VALIDADE_CODIGO), 0));
    }

    public void redefinirSenha(String username, String codigo, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (codigo == null || !codigo.matches("\\d{6}")) {
            throw new IllegalArgumentException("O código de recuperação é inválido ou expirou.");
        }
        String chave = normalizarUsername(username);
        CodigoRecuperacao guardado = codigos.get(chave);
        if (guardado == null || Instant.now().isAfter(guardado.expiraEm())) {
            codigos.remove(chave);
            throw new IllegalArgumentException("O código de recuperação é inválido ou expirou.");
        }
        if (!guardado.valor().equals(codigo)) {
            int tentativas = guardado.tentativas() + 1;
            if (tentativas >= MAX_TENTATIVAS) {
                codigos.remove(chave, guardado);
            } else {
                codigos.replace(chave, guardado,
                        new CodigoRecuperacao(guardado.valor(), guardado.expiraEm(), tentativas));
            }
            throw new IllegalArgumentException("O código de recuperação é inválido ou expirou.");
        }
        Utilizador utilizador = repositorio.buscarPorUsername(username.trim())
                .filter(Utilizador::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O código de recuperação é inválido ou expirou."));
        utilizadorService.alterarSenha(utilizador.getId(), novaSenha);
        codigos.remove(chave, guardado);
    }

    private String normalizarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O username é obrigatório.");
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private record CodigoRecuperacao(String valor, Instant expiraEm, int tentativas) {
    }
}
