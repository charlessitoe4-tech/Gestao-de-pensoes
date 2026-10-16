package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import model.Beneficiario;
import model.enums.EstadoCivil;
import model.enums.Genero;
import org.junit.jupiter.api.Test;
import repository.RepositorioEmMemoria;

class BeneficiarioServiceTest {

    private static final class RepositorioComIds
            extends RepositorioEmMemoria<Beneficiario, Long> {

        private long proximoId;

        private RepositorioComIds() {
            super(Beneficiario::getId);
        }

        @Override
        public synchronized void criar(Beneficiario beneficiario) {
            if (beneficiario.getId() == null) {
                beneficiario.setId(++proximoId);
            }
            super.criar(beneficiario);
        }
    }

    @Test
    void registaCrudEPermiteDesfazerUltimaRemocao() {
        BeneficiarioService servico = new BeneficiarioService(new RepositorioComIds());
        Beneficiario novo = new Beneficiario();
        novo.setNome("Ana");
        novo.setApelido("Mussa");
        novo.setNumeroBI("123456789012");
        novo.setTelefone("840000000");
        novo.setDataNascimento(LocalDate.of(1990, 1, 1));
        novo.setGenero(Genero.FEMININO);
        novo.setEstadoCivil(EstadoCivil.SOLTEIRO);

        Beneficiario guardado = servico.guardar(novo);
        assertTrue(servico.remover(guardado.getId()));
        Beneficiario restaurado = servico.desfazerUltimaRemocao().orElseThrow();

        assertEquals(2L, restaurado.getId());
        assertEquals("123456789012", restaurado.getNumeroBI());
        assertEquals(3, servico.consultarHistorico().size());
        assertEquals(1, servico.listar().size());
    }

    @Test
    void rejeitaDataDeNascimentoFutura() {
        BeneficiarioService servico = new BeneficiarioService(new RepositorioComIds());
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome("Ana");
        beneficiario.setNumeroBI("123456789012");
        beneficiario.setTelefone("840000000");
        beneficiario.setDataNascimento(LocalDate.now().plusDays(1));
        beneficiario.setGenero(Genero.FEMININO);
        beneficiario.setEstadoCivil(EstadoCivil.SOLTEIRO);

        assertThrows(IllegalArgumentException.class, () -> servico.guardar(beneficiario));
    }
}
