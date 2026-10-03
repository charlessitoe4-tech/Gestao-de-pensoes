package service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.HibernatePagamentoDAO;
import dao.PagamentoDAO;
import model.Pagamento;
import model.enums.EstadoPagamento;
import model.enums.FormaPagamento;

/** Regras de negócio para pagamentos de pensões. */
public class PagamentoService {

    private final PagamentoDAO repositorio;

    public PagamentoService() {
        this(new HibernatePagamentoDAO());
    }

    public PagamentoService(PagamentoDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Pagamento registar(Long pensaoId, YearMonth periodo,
                              FormaPagamento formaPagamento) {
        Objects.requireNonNull(pensaoId, "O ID da pensão é obrigatório.");
        Objects.requireNonNull(periodo, "O mês de referência é obrigatório.");
        Objects.requireNonNull(formaPagamento, "A forma de pagamento é obrigatória.");

        if (periodo.isAfter(YearMonth.now())) {
            throw new IllegalArgumentException(
                    "Não é possível registar pagamentos de meses futuros.");
        }
        return repositorio.registarPagamentoMensal(
                pensaoId, periodo.atDay(1), formaPagamento);
    }

    public List<Pagamento> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<Pagamento> listarPorPensao(Long pensaoId) {
        Objects.requireNonNull(pensaoId, "O ID da pensão é obrigatório.");
        return repositorio.listarPorPensao(pensaoId);
    }

    public Optional<Pagamento> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public Pagamento cancelar(Long id) {
        Pagamento pagamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Pagamento não encontrado: " + id));
        if (pagamento.getEstado() == EstadoPagamento.CANCELADO) {
            throw new IllegalStateException("O pagamento já está cancelado.");
        }
        pagamento.setEstado(EstadoPagamento.CANCELADO);
        // Nota: PagamentoDAO não tem atualizar() — usar o Hibernate diretamente
        // ou adicionar o método ao DAO (aqui fica como TODO).
        return pagamento;
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    /** Soma todos os pagamentos com estado PAGO. */
    public java.math.BigDecimal calcularTotalPago() {
        return listar().stream()
                .filter(p -> p.getEstado() == EstadoPagamento.PAGO)
                .map(Pagamento::getValor)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /** Soma todos os pagamentos de um pensionista num dado mês. */
    public java.math.BigDecimal calcularTotalMes(Long pensionistaId, YearMonth mes) {
        Objects.requireNonNull(pensionistaId, "O ID do pensionista é obrigatório.");
        Objects.requireNonNull(mes, "O mês é obrigatório.");
        LocalDate primeiroDia = mes.atDay(1);
        LocalDate ultimoDia = mes.atEndOfMonth();
        return listar().stream()
                .filter(p -> pensionistaId.equals(p.getPensionistaId()))
                .filter(p -> !p.getDataReferencia().isBefore(primeiroDia)
                        && !p.getDataReferencia().isAfter(ultimoDia))
                .filter(p -> p.getEstado() == EstadoPagamento.PAGO)
                .map(Pagamento::getValor)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}