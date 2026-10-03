package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import dao.PagamentoDAO;
import dao.PensaoDAO;
import model.Pagamento;
import model.Pensao;
import model.PensaoInvalidez;
import model.PensaoReduzida;
import model.PensaoSobrivivencia;
import model.PensaoVelhice;
import model.enums.FormaPagamento;
import model.enums.TipoPensao;
import org.junit.jupiter.api.Test;
import repository.RepositorioEmMemoria;
import service.PagamentoPensaoService;
import service.PensaoService;

class PensaoControllerTest {

    private static final class PensaoDAOFake
            extends RepositorioEmMemoria<Pensao, Long> implements PensaoDAO {

        private long proximoId;

        private PensaoDAOFake() {
            super(Pensao::getId);
        }

        @Override
        public synchronized void criar(Pensao pensao) {
            if (pensao.getId() == null) {
                pensao.setId(++proximoId);
            }
            super.criar(pensao);
        }
    }

    private static final class PagamentoDAOFake implements PagamentoDAO {

        @Override
        public Pagamento registarPagamentoMensal(
                Long pensaoId, LocalDate referencia, FormaPagamento formaPagamento) {
            Pagamento pagamento = new Pagamento();
            pagamento.setPensaoId(pensaoId);
            pagamento.setDataReferencia(referencia);
            pagamento.setFormaPagamento(formaPagamento);
            return pagamento;
        }

        @Override
        public List<Pagamento> listarPorPensao(Long pensaoId) {
            return List.of();
        }
    }

    @Test
    void criaAClassePolimorficaAdequadaParaCadaTipo() {
        PensaoController controller = criarController();

        assertEquals(PensaoVelhice.class, criar(controller, TipoPensao.VELHICE).getClass());
        assertEquals(PensaoInvalidez.class, criar(controller, TipoPensao.INVALIDEZ).getClass());
        assertEquals(PensaoSobrivivencia.class,
                criar(controller, TipoPensao.SOBREVIVENCIA).getClass());
        assertEquals(PensaoReduzida.class, criar(controller, TipoPensao.REDUZIDA).getClass());
    }

    @Test
    void naoPermiteMudarTipoDeUmaPensaoExistente() {
        PensaoController controller = criarController();
        Pensao pensao = criar(controller, TipoPensao.VELHICE);

        assertThrows(IllegalArgumentException.class,
                () -> controller.guardar(pensao.getId(), "PROC-2", TipoPensao.INVALIDEZ,
                        1L, new BigDecimal("1500"), LocalDate.of(2026, 1, 1), ""));
    }

    @Test
    void delegaRegistoDePagamentoComPeriodoEFormaSelecionados() {
        PensaoController controller = criarController();
        YearMonth periodo = YearMonth.now();

        Pagamento pagamento = controller.registarPagamento(
                1L, periodo, FormaPagamento.MOVEL);

        assertEquals(1L, pagamento.getPensaoId());
        assertEquals(periodo.atDay(1), pagamento.getDataReferencia());
        assertEquals(FormaPagamento.MOVEL, pagamento.getFormaPagamento());
    }

    private Pensao criar(PensaoController controller, TipoPensao tipo) {
        return controller.guardar(null, "PROC-" + tipo, tipo, 1L,
                new BigDecimal("1500.00"), LocalDate.of(2026, 1, 1), "");
    }

    private PensaoController criarController() {
        PensaoDAOFake pensaoDAO = new PensaoDAOFake();
        PagamentoPensaoService pagamentoService =
                new PagamentoPensaoService(new PagamentoDAOFake());
        return new PensaoController(new PensaoService(pensaoDAO), pagamentoService);
    }
}
