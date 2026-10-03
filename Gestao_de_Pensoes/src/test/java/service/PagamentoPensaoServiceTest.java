package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import dao.PagamentoDAO;
import model.Pagamento;
import model.enums.FormaPagamento;
import org.junit.jupiter.api.Test;

class PagamentoPensaoServiceTest {

    @Test
    void registaPeriodoComoPrimeiroDiaDoMes() {
        PagamentoDAOFake repositorio = new PagamentoDAOFake();
        PagamentoPensaoService servico = new PagamentoPensaoService(repositorio);

        servico.registar(3L, YearMonth.of(2026, 2), FormaPagamento.MOVEL);

        assertEquals(LocalDate.of(2026, 2, 1), repositorio.referencia);
        assertEquals(3L, repositorio.pensaoId);
        assertEquals(FormaPagamento.MOVEL, repositorio.formaPagamento);
    }

    @Test
    void rejeitaPeriodoFuturoAntesDeChamarRepositorio() {
        PagamentoDAOFake repositorio = new PagamentoDAOFake();
        PagamentoPensaoService servico = new PagamentoPensaoService(repositorio);

        assertThrows(IllegalArgumentException.class,
                () -> servico.registar(3L, YearMonth.now().plusMonths(1),
                        FormaPagamento.TRANSFERENCIA_BANCARIA));
        assertEquals(null, repositorio.referencia);
    }

    private static final class PagamentoDAOFake implements PagamentoDAO {

        private Long pensaoId;
        private LocalDate referencia;
        private FormaPagamento formaPagamento;

        @Override
        public Pagamento registarPagamentoMensal(
                Long pensaoId, LocalDate referencia, FormaPagamento formaPagamento) {
            this.pensaoId = pensaoId;
            this.referencia = referencia;
            this.formaPagamento = formaPagamento;
            return new Pagamento();
        }

        @Override
        public Optional<Pagamento> buscarPorId(Long id) {
            return Optional.empty();
        }

        @Override
        public List<Pagamento> listarTodos() {
            return List.of();
        }

        @Override
        public List<Pagamento> listarPorPensao(Long pensaoId) {
            return List.of();
        }

        @Override
        public void atualizar(Long id, Pagamento pagamento) {
        }

        @Override
        public boolean remover(Long id) {
            return false;
        }
    }
}
