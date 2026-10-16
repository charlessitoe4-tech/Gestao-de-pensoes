package service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import dao.PerfilDAO;
import dao.UtilizadorDAO;
import model.Perfil;
import model.enums.NivelAcesso;

/** Prepara os três perfis do sistema e permite criar o primeiro administrador. */
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
            if (utilizadorDAO.buscarPorUsername(conta.getKey()).isEmpty()) {
                emFalta.put(conta.getKey(), conta.getValue());
            }
        }
        return Map.copyOf(emFalta);
    }

    /**
     * Garante os três utilizadores principais. As senhas das contas em falta
     * são definidas no primeiro arranque e nunca ficam guardadas em código.
     */
    public void assegurarContasPrincipais(Map<String, String> senhas) {
        Objects.requireNonNull(senhas, "As senhas iniciais são obrigatórias.");
        for (Map.Entry<String, NivelAcesso> conta : contasPrincipais().entrySet()) {
            Perfil perfil = perfilDAO.buscarPorNome(nomePerfil(conta.getValue()))
                    .orElseThrow(() -> new IllegalStateException(
                            "O perfil " + nomePerfil(conta.getValue()) + " não existe."));
            model.Utilizador utilizador = utilizadorDAO.buscarPorUsername(conta.getKey())
                    .orElseGet(() -> {
                        String senha = senhas.get(conta.getKey());
                        if (senha == null || senha.length() < 10) {
                            throw new IllegalArgumentException(
                                    "Defina uma senha com pelo menos 10 caracteres para "
                                    + conta.getKey() + ".");
                        }
                        model.Utilizador novo = new model.Utilizador();
                        novo.setNome(nomeUtilizador(conta.getValue()));
                        novo.setUsername(conta.getKey());
                        novo.setSenhaHash(Util.PasswordUtil.gerarHash(senha));
                        novo.setPerfil(perfil);
                        novo.setAtivo(true);
                        return novo;
                    });
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
