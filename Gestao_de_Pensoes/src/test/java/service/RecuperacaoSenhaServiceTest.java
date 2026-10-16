package service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dao.UtilizadorDAO;
import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;
import org.junit.jupiter.api.Test;
import Util.PasswordUtil;

class RecuperacaoSenhaServiceTest {

    @Test
    void enviaCodigoEPermiteRedefinirSenha() {
        UtilizadorDAOFake dao = new UtilizadorDAOFake();
        Utilizador utilizador = utilizador("Ana", "ana@example.com");
        dao.criar(utilizador);
        List<String> mensagens = new ArrayList<>();
        RecuperacaoSenhaService recuperacao = new RecuperacaoSenhaService(
                dao, new UtilizadorService(dao),
                (destinatario, assunto, mensagem) -> mensagens.add(mensagem));

        recuperacao.solicitarCodigo("ANA");
        String codigo = mensagens.getFirst().replaceAll("(?s).*?(\\d{6}).*", "$1");
        recuperacao.redefinirSenha("ana", codigo, "senha-nova-8");

        assertTrue(PasswordUtil.verificar("senha-nova-8", utilizador.getSenhaHash()));
        assertFalse(PasswordUtil.verificar("senha-antiga", utilizador.getSenhaHash()));
        assertThrows(IllegalArgumentException.class,
                () -> recuperacao.redefinirSenha("ana", codigo, "outra-senha"));
    }

    @Test
    void bloqueiaCodigoDepoisDeCincoTentativasEExigeSenhaMinima() {
        UtilizadorDAOFake dao = new UtilizadorDAOFake();
        dao.criar(utilizador("Ana", "ana@example.com"));
        List<String> mensagens = new ArrayList<>();
        RecuperacaoSenhaService recuperacao = new RecuperacaoSenhaService(
                dao, new UtilizadorService(dao),
                (destinatario, assunto, mensagem) -> mensagens.add(mensagem));
        recuperacao.solicitarCodigo("Ana");

        assertThrows(IllegalArgumentException.class,
                () -> recuperacao.redefinirSenha("Ana", "123", "curta"));
        for (int tentativa = 0; tentativa < 5; tentativa++) {
            assertThrows(IllegalArgumentException.class,
                    () -> recuperacao.redefinirSenha("Ana",
                            "000000".equals(codigo(mensagens)) ? "000001" : "000000",
                            "senha-nova-8"));
        }
        assertThrows(IllegalArgumentException.class,
                () -> recuperacao.redefinirSenha("Ana", codigo(mensagens), "senha-nova-8"));
    }

    private Utilizador utilizador(String nome, String email) {
        Perfil perfil = new Perfil();
        perfil.setNome("Operador");
        perfil.setNivelAcesso(NivelAcesso.OPERADOR);
        Utilizador utilizador = new Utilizador();
        utilizador.setNome(nome);
        utilizador.setUsername(nome);
        utilizador.setEmail(email);
        utilizador.setSenhaHash(PasswordUtil.gerarHash("senha-antiga"));
        utilizador.setPerfil(perfil);
        utilizador.setAtivo(true);
        return utilizador;
    }

    private String codigo(List<String> mensagens) {
        return mensagens.getFirst().replaceAll("(?s).*?(\\d{6}).*", "$1");
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
