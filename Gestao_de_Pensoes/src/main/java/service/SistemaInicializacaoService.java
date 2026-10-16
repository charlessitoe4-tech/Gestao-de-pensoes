package service;

import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dao.PerfilDAO;
import dao.UtilizadorDAO;
import model.Perfil;
import model.enums.NivelAcesso;

/** Prepara os três perfis do sistema e as respetivas contas principais. */
public final class SistemaInicializacaoService {

    private static final List<NivelAcesso> PERFIS_INICIAIS = List.of(
            NivelAcesso.ADMINISTRADOR,
            NivelAcesso.GESTOR,
            NivelAcesso.OPERADOR);

    private final PerfilDAO perfilDAO;
    private final UtilizadorDAO utilizadorDAO;
    private final UtilizadorService utilizadorService;

    public SistemaInicializacaoService() {
        this(new dao.HibernatePerfilDAO(), new dao.HibernateUtilizadorDAO(),
                new UtilizadorService());
    }

    public SistemaInicializacaoService(PerfilDAO perfilDAO, UtilizadorDAO utilizadorDAO,
            UtilizadorService utilizadorService) {
        this.perfilDAO = Objects.requireNonNull(perfilDAO, "O DAO de perfis é obrigatório.");
        this.utilizadorDAO = Objects.requireNonNull(utilizadorDAO,
                "O DAO de utilizadores é obrigatório.");
        this.utilizadorService = Objects.requireNonNull(utilizadorService,
                "O serviço de utilizadores é obrigatório.");
    }

    public void assegurarPerfis() {
        for (NivelAcesso nivel : PERFIS_INICIAIS) {
            String nome = nomePerfil(nivel);
            Perfil perfil = perfilDAO.buscarPorNome(nome).orElseGet(() -> {
                Perfil novo = new Perfil();
                novo.setNome(nome);
                novo.setDescricao("Perfil " + nome + " da aplicação.");
                novo.setNivelAcesso(nivel);
                perfilDAO.criar(novo);
                return novo;
            });
            if (perfil.getNivelAcesso() != nivel) {
                perfil.setNivelAcesso(nivel);
                perfilDAO.atualizar(perfil.getId(), perfil);
            }
        }
    }

    public boolean necessitaAdministradorInicial() {
        return utilizadorDAO.listarTodos().isEmpty();
    }

    public Map<String, NivelAcesso> contasPrincipaisEmFalta() {
        Map<String, NivelAcesso> emFalta = new LinkedHashMap<>();
        for (Map.Entry<String, NivelAcesso> conta : contasPrincipais().entrySet()) {
            model.Utilizador utilizador = utilizadorDAO.buscarPorUsername(conta.getKey()).orElse(null);
            if (utilizador == null
                    || !ValidacaoDadosMoçambique.emailValido(utilizador.getEmail())) {
                emFalta.put(conta.getKey(), conta.getValue());
            }
        }
        return Collections.unmodifiableMap(emFalta);
    }

    public boolean necessitaSenhaInicial(String username) {
        return utilizadorDAO.buscarPorUsername(username).isEmpty();
    }

    /**
     * Garante os três utilizadores principais. As senhas das contas em falta
     * são definidas no primeiro arranque e nunca ficam guardadas em código.
     */
    public void assegurarContasPrincipais(Map<String, String> senhas,
            Map<String, String> emails) {
        Objects.requireNonNull(senhas, "As senhas iniciais são obrigatórias.");
        Objects.requireNonNull(emails, "Os emails de recuperação são obrigatórios.");
        Map<String, Perfil> perfis = new LinkedHashMap<>();
        for (Map.Entry<String, NivelAcesso> conta : contasPrincipais().entrySet()) {
            Perfil perfil = perfilDAO.buscarPorNome(nomePerfil(conta.getValue()))
                    .orElseThrow(() -> new IllegalStateException(
                            "O perfil " + nomePerfil(conta.getValue()) + " não existe."));
            perfis.put(conta.getKey(), perfil);
            if (utilizadorDAO.buscarPorUsername(conta.getKey()).isEmpty()) {
                String senha = senhas.get(conta.getKey());
                if (senha == null || senha.length() < 8) {
                    throw new IllegalArgumentException(
                            "Defina uma senha com pelo menos 8 caracteres para "
                            + conta.getKey() + ".");
                }
            }
            String email = emails.get(conta.getKey());
            if (email != null && !ValidacaoDadosMoçambique.emailValido(email)) {
                throw new IllegalArgumentException("O email da conta " + conta.getKey()
                        + " não é válido.");
            }
            model.Utilizador existente = utilizadorDAO.buscarPorUsername(conta.getKey()).orElse(null);
            if ((existente == null || !ValidacaoDadosMoçambique.emailValido(existente.getEmail()))
                    && !ValidacaoDadosMoçambique.emailValido(email)) {
                throw new IllegalArgumentException("Defina um email válido para a conta "
                        + conta.getKey() + ".");
            }
        }
        for (Map.Entry<String, NivelAcesso> conta : contasPrincipais().entrySet()) {
            Perfil perfil = perfis.get(conta.getKey());
            model.Utilizador utilizador = utilizadorDAO.buscarPorUsername(conta.getKey())
                    .orElseGet(() -> {
                        model.Utilizador novo = new model.Utilizador();
                        novo.setNome(nomeUtilizador(conta.getValue()));
                        novo.setUsername(conta.getKey());
                        novo.setSenhaHash(Util.PasswordUtil.gerarHash(senhas.get(conta.getKey())));
                        novo.setEmail(emails.get(conta.getKey()).trim());
                        novo.setPerfil(perfil);
                        novo.setAtivo(true);
                        return novo;
                    });
            if (emails.containsKey(conta.getKey())) {
                utilizador.setEmail(emails.get(conta.getKey()).trim());
            }
            utilizador.setPerfil(perfil);
            utilizadorService.guardarContaPrincipal(utilizador, conta.getValue());
        }
    }

    private Map<String, NivelAcesso> contasPrincipais() {
        Map<String, NivelAcesso> contas = new LinkedHashMap<>();
        contas.put("admin", NivelAcesso.ADMINISTRADOR);
        contas.put("gestor", NivelAcesso.GESTOR);
        contas.put("operador", NivelAcesso.OPERADOR);
        return contas;
    }

    private String nomeUtilizador(NivelAcesso nivel) {
        return switch (nivel) {
            case ADMINISTRADOR -> "Administrador Principal";
            case GESTOR -> "Gestor Principal";
            case OPERADOR -> "Operador Principal";
        };
    }

    private String nomePerfil(NivelAcesso nivel) {
        return switch (nivel) {
            case ADMINISTRADOR -> "Administrador";
            case GESTOR -> "Gestor";
            case OPERADOR -> "Operador";
        };
    }
}
