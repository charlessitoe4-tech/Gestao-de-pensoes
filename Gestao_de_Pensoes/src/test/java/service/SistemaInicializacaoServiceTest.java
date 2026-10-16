package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import dao.PerfilDAO;
import dao.UtilizadorDAO;
import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;
import org.junit.jupiter.api.Test;

class SistemaInicializacaoServiceTest {

    @Test
    void inicializaTresPerfisEProtegeAsTresContasPrincipais() {
        PerfilDAOFake perfis = new PerfilDAOFake();
        UtilizadorDAOFake utilizadores = new UtilizadorDAOFake();
        UtilizadorService servicoUtilizadores = new UtilizadorService(utilizadores);
        SistemaInicializacaoService inicializacao = new SistemaInicializacaoService(
                perfis, utilizadores, servicoUtilizadores);

        inicializacao.assegurarPerfis();
        inicializacao.assegurarPerfis();
        assertEquals(3, perfis.listarTodos().size());
        assertEquals(Map.of(
                "admin", NivelAcesso.ADMINISTRADOR,
                "gestor", NivelAcesso.GESTOR,
                "operador", NivelAcesso.OPERADOR),
                inicializacao.contasPrincipaisEmFalta());

        inicializacao.assegurarContasPrincipais(Map.of(
                "admin", "senha123",
                "gestor", "senha456",
                "operador", "senha789"), Map.of(
                "admin", "admin@example.com",
                "gestor", "gestor@example.com",
                "operador", "operador@example.com"));

        assertTrue(inicializacao.contasPrincipaisEmFalta().isEmpty());
        assertEquals(3, utilizadores.listarTodos().size());
        for (Utilizador conta : utilizadores.listarTodos()) {
            assertTrue(conta.isContaProtegida());
            assertTrue(conta.isAtivo());
            assertTrue(conta.getSenhaHash().startsWith("pbkdf2-sha256$"));
            assertThrows(IllegalStateException.class,
                    () -> servicoUtilizadores.remover(conta.getId()));
        }
        assertEquals("Admin", utilizadores.buscarPorUsername("ADMIN").orElseThrow().getUsername());
        assertEquals(NivelAcesso.ADMINISTRADOR,
                utilizadores.buscarPorUsername("admin").orElseThrow()
                        .getPerfil().getNivelAcesso());
        assertEquals(NivelAcesso.GESTOR,
                utilizadores.buscarPorUsername("gestor").orElseThrow()
                        .getPerfil().getNivelAcesso());
        assertEquals(NivelAcesso.OPERADOR,
                utilizadores.buscarPorUsername("operador").orElseThrow()
                        .getPerfil().getNivelAcesso());
    }

    @Test
    void exigeSenhaForteParaContasPrincipaisEmFalta() {
        PerfilDAOFake perfis = new PerfilDAOFake();
        UtilizadorDAOFake utilizadores = new UtilizadorDAOFake();
        SistemaInicializacaoService inicializacao = new SistemaInicializacaoService(
                perfis, utilizadores, new UtilizadorService(utilizadores));
        inicializacao.assegurarPerfis();

        assertThrows(IllegalArgumentException.class,
                () -> inicializacao.assegurarContasPrincipais(
                        Map.of("admin", "curta"), Map.of("admin", "admin@example.com")));
    }

    private static final class PerfilDAOFake implements PerfilDAO {

        private final List<Perfil> perfis = new ArrayList<>();
        private long proximoId;

        @Override
        public void criar(Perfil perfil) {
            perfil.setId(++proximoId);
            perfis.add(perfil);
        }

        @Override
        public Optional<Perfil> buscarPorId(Long id) {
            return perfis.stream().filter(p -> p.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Perfil> buscarPorNome(String nome) {
            return perfis.stream().filter(p -> p.getNome().equals(nome)).findFirst();
        }

        @Override
        public List<Perfil> listarTodos() {
            return new ArrayList<>(perfis);
        }

        @Override
        public void atualizar(Long id, Perfil perfil) {
            if (buscarPorId(id).isEmpty()) {
                throw new IllegalArgumentException("Perfil inexistente.");
            }
        }

        @Override
        public boolean remover(Long id) {
            return perfis.removeIf(p -> p.getId().equals(id));
        }
    }

    private static final class UtilizadorDAOFake implements UtilizadorDAO {

        private final List<Utilizador> contas = new ArrayList<>();
        private long proximoId;

        @Override
        public void criar(Utilizador utilizador) {
            utilizador.setId(++proximoId);
            contas.add(utilizador);
        }

        @Override
        public Optional<Utilizador> buscarPorId(Long id) {
            return contas.stream().filter(u -> u.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Utilizador> buscarPorUsername(String username) {
            return contas.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
        }

        @Override
        public List<Utilizador> listarTodos() {
            return new ArrayList<>(contas);
        }

        @Override
        public void atualizar(Long id, Utilizador utilizador) {
            if (buscarPorId(id).isEmpty()) {
                throw new IllegalArgumentException("Utilizador inexistente.");
            }
        }

        @Override
        public boolean remover(Long id) {
            return contas.removeIf(u -> u.getId().equals(id));
        }
    }
}
