package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import Util.PasswordUtil;
import dao.UtilizadorDAO;
import model.Utilizador;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    @Test
    void autenticaUtilizadorAtivoERegistaUltimoLogin() {
        Utilizador utilizador = utilizadorAtivo();
        UtilizadorDAOFake repositorio = new UtilizadorDAOFake(utilizador);
        LoginService servico = new LoginService(repositorio);

        Optional<Utilizador> autenticado = servico.autenticar(" utilizador ", "senha-correta");

        assertTrue(autenticado.isPresent());
        assertEquals(utilizador, autenticado.orElseThrow());
        assertNotNull(utilizador.getUltimoLogin());
        assertEquals(1, repositorio.quantidadeAtualizacoes);
    }

    @Test
    void rejeitaSenhaIncorretaSemAtualizarUtilizador() {
        UtilizadorDAOFake repositorio = new UtilizadorDAOFake(utilizadorAtivo());
        LoginService servico = new LoginService(repositorio);

        assertTrue(servico.autenticar("utilizador", "senha-incorreta").isEmpty());
        assertEquals(0, repositorio.quantidadeAtualizacoes);
    }

    @Test
    void rejeitaUtilizadorInativoESenhaVazia() {
        Utilizador utilizador = utilizadorAtivo();
        utilizador.setAtivo(false);
        UtilizadorDAOFake repositorio = new UtilizadorDAOFake(utilizador);
        LoginService servico = new LoginService(repositorio);

        assertFalse(servico.autenticar("utilizador", "senha-correta").isPresent());
        assertTrue(servico.autenticar("utilizador", " ").isEmpty());
        assertEquals(0, repositorio.quantidadeAtualizacoes);
    }

    private static Utilizador utilizadorAtivo() {
        Utilizador utilizador = new Utilizador();
        utilizador.setId(1L);
        utilizador.setUsername("utilizador");
        utilizador.setSenhaHash(PasswordUtil.gerarHash("senha-correta"));
        utilizador.setAtivo(true);
        return utilizador;
    }

    private static final class UtilizadorDAOFake implements UtilizadorDAO {

        private final Utilizador utilizador;
        private int quantidadeAtualizacoes;

        private UtilizadorDAOFake(Utilizador utilizador) {
            this.utilizador = utilizador;
        }

        @Override
        public void criar(Utilizador utilizador) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Utilizador> buscarPorId(Long id) {
            return this.utilizador.getId().equals(id)
                    ? Optional.of(this.utilizador)
                    : Optional.empty();
        }

        @Override
        public Optional<Utilizador> buscarPorUsername(String username) {
            return this.utilizador.getUsername().equals(username)
                    ? Optional.of(this.utilizador)
                    : Optional.empty();
        }

        @Override
        public List<Utilizador> listarTodos() {
            return List.of(utilizador);
        }

        @Override
        public void atualizar(Long id, Utilizador utilizador) {
            assertEquals(this.utilizador.getId(), id);
            assertEquals(this.utilizador, utilizador);
            quantidadeAtualizacoes++;
        }

        @Override
        public boolean remover(Long id) {
            return false;
        }
    }
}
