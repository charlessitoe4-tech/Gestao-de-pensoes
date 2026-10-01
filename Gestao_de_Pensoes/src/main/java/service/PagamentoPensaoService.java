package service;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import dao.HibernatePagamentoDAO;
import dao.PagamentoDAO;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Regras de aplicação para liquidações mensais de pensão. */
public class PagamentoPensaoService {

    private final PagamentoDAO repositorio;

    public PagamentoPensaoService() {
        this(new HibernatePagamentoDAO());
    }

    public PagamentoPensaoService(PagamentoDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Pagamento registar(Long pensaoId, YearMonth periodo, FormaPagamento formaPagamento) {
        Objects.requireNonNull(periodo, "O mês de referência é obrigatório.");
        if (periodo.isAfter(YearMonth.now())) {
            throw new IllegalArgumentException("Não é possível registar pagamentos de meses futuros.");
        }
        return repositorio.registarPagamentoMensal(
                pensaoId, periodo.atDay(1), formaPagamento);
    }

    public List<Pagamento> listar(Long pensaoId) {
        return repositorio.listarPorPensao(pensaoId);
    }
}
