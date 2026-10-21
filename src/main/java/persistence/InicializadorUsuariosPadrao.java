package persistence;

import java.time.LocalDateTime;
import Util.PasswordUtil;
import dao.HibernatePerfilDAO;
import dao.HibernateUtilizadorDAO;
import dao.PerfilDAO;
import dao.UtilizadorDAO;
import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;

/** Garante os 3 utilizadores predefinidos (admin, gestor, operador), sempre ativos. */
public final class InicializadorUsuariosPadrao {
    private InicializadorUsuariosPadrao() {}

    public static void inicializar() {
        PerfilDAO perfis = new HibernatePerfilDAO();
        UtilizadorDAO utilizadores = new HibernateUtilizadorDAO();

        Perfil admin = obterOuCriarPerfil(perfis, "Administrador", "Acesso completo ao sistema", NivelAcesso.ADMINISTRADOR);
        Perfil gestor = obterOuCriarPerfil(perfis, "Gestor", "Gestão de processos, pensões, pagamentos e relatórios", NivelAcesso.GESTOR);
        Perfil operador = obterOuCriarPerfil(perfis, "Operador", "Registo e consulta de dados operacionais", NivelAcesso.OPERADOR);

        garantirUtilizador(utilizadores, "Administrador do Sistema", "admin", "admin@pensoes.local", "Admin@123", admin);
        garantirUtilizador(utilizadores, "Gestor do Sistema", "gestor", "gestor@pensoes.local", "Gestor@123", gestor);
        garantirUtilizador(utilizadores, "Operador do Sistema", "operador", "operador@pensoes.local", "Operador@123", operador);
    }

    private static Perfil obterOuCriarPerfil(PerfilDAO dao, String nome, String descricao, NivelAcesso nivel) {
        return dao.buscarPorNome(nome).orElseGet(() -> {
            Perfil p = new Perfil(null, nome, descricao, nivel);
            dao.criar(p);
            return dao.buscarPorNome(nome).orElseThrow(() -> new IllegalStateException("Não foi possível criar o perfil " + nome));
        });
    }

    /**
     * Garante que a conta predefinida existe e está ativa com o perfil correto.
     * Se a conta já existir, é reativada e a senha volta à predefinida.
     */
    private static void garantirUtilizador(UtilizadorDAO dao, String nome, String username, String email, String senha, Perfil perfil) {
        java.util.Optional<Utilizador> existente = dao.buscarPorUsername(username);
        if (existente.isPresent()) {
            Utilizador u = existente.get();
            boolean alterado = false;
            if (!u.isAtivo()) {
                u.setAtivo(true);
                alterado = true;
            }
            if (u.getPerfil() == null || !perfil.getId().equals(u.getPerfil().getId())) {
                u.setPerfil(perfil);
                alterado = true;
            }
            if (!PasswordUtil.verificar(senha, u.getSenhaHash())) {
                u.setSenhaHash(PasswordUtil.gerarHash(senha));
                alterado = true;
            }
            if (alterado) {
                dao.atualizar(u.getId(), u);
            }
            return;
        }
        Utilizador u = new Utilizador();
        u.setNome(nome);
        u.setUsername(username);
        u.setEmail(email);
        u.setSenhaHash(PasswordUtil.gerarHash(senha));
        u.setPerfil(perfil);
        u.setAtivo(true);
        u.setDataCriacao(LocalDateTime.now());
        dao.criar(u);
    }
}
