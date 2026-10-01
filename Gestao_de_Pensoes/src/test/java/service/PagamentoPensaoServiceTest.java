package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import model.Pagamento;
import model.enums.FormaPagamento;
import org.junit.jupiter.api.Test;
import repository.PagamentoPensaoRepository;

class PagamentoPensaoServiceTest {

    @Test
    void registaPeriodoComoPrimeiroDiaDoMes() {
        PagamentoPensaoRepositoryFake repositorio = new PagamentoPensaoRepositoryFake();
        PagamentoPensaoService servico = new PagamentoPensaoService(repositorio);

        servico.registar(3L, YearMonth.of(2026, 2), FormaPagamento.MOVEL);

        assertEquals(LocalDate.of(2026, 2, 1), repositorio.referencia);
        assertEquals(3L, repositorio.pensaoId);
        assertEquals(FormaPagamento.MOVEL, repositorio.formaPagamento);
    }

    @Test
    void rejeitaPeriodoFuturoAntesDeChamarRepositorio() {
        PagamentoPensaoRepositoryFake repositorio = new PagamentoPensaoRepositoryFake();
        PagamentoPensaoService servico = new PagamentoPensaoService(repositorio);

        assertThrows(IllegalArgumentException.class,
                () -> servico.registar(3L, YearMonth.now().plusMonths(1),
                        FormaPagamento.TRANSFERENCIA_BANCARIA));
        assertEquals(null, repositorio.referencia);
    }

    private static final class PagamentoPensaoRepositoryFake
            implements PagamentoPensaoRepository {

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
        public List<Pagamento> listarPorPensao(Long pensaoId) {
            return List.of();
        }
    }
}
