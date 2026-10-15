package service;

import java.util.List;
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

    public model.Utilizador criarAdministradorInicial(String nome, String username,
            String email, String senha) {
        if (!necessitaAdministradorInicial()) {
            throw new IllegalStateException("O administrador inicial já foi criado.");
        }
        Perfil perfil = perfilDAO.buscarPorNome(nomePerfil(NivelAcesso.ADMINISTRADOR))
                .orElseThrow(() -> new IllegalStateException("O perfil de administrador não existe."));
        return utilizadorService.registar(nome, username, email, senha, perfil);
    }

    private String nomePerfil(NivelAcesso nivel) {
        return switch (nivel) {
            case ADMINISTRADOR -> "Administrador";
            case GESTOR -> "Gestor";
            case OPERADOR -> "Operador";
        };
    }
}
