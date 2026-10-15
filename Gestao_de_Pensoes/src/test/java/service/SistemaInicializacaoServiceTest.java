package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dao.PerfilDAO;
import dao.UtilizadorDAO;
import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;
import org.junit.jupiter.api.Test;

class SistemaInicializacaoServiceTest {

    @Test
    void inicializaTresPerfisEPermiteCriarOAdministradorUmaUnicaVez() {
        PerfilDAOFake perfis = new PerfilDAOFake();
        UtilizadorDAOFake utilizadores = new UtilizadorDAOFake();
        SistemaInicializacaoService inicializacao = new SistemaInicializacaoService(
                perfis, utilizadores, new UtilizadorService(utilizadores));

        inicializacao.assegurarPerfis();
        inicializacao.assegurarPerfis();
        assertEquals(3, perfis.listarTodos().size());
        assertTrue(inicializacao.necessitaAdministradorInicial());

        Utilizador administrador = inicializacao.criarAdministradorInicial(
                "Admin Principal", "admin", "", "senha-muito-segura");

        assertEquals(NivelAcesso.ADMINISTRADOR,
                administrador.getPerfil().getNivelAcesso());
        assertTrue(administrador.getSenhaHash().startsWith("pbkdf2-sha256$"));
        assertFalse(inicializacao.necessitaAdministradorInicial());
        assertThrows(IllegalStateException.class, () ->
                inicializacao.criarAdministradorInicial(
                        "Outro Admin", "admin2", "", "senha-muito-segura"));
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
            return contas.stream().filter(u -> u.getUsername().equals(username)).findFirst();
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
