package controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Pagamento;
import model.enums.FormaPagamento;
import service.PagamentoService;

/** Controlador de aplicação para pagamentos de pensões. */
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController() {
        this(new PagamentoService());
    }

    public PagamentoController(PagamentoService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Pagamento registar(Long pensaoId, YearMonth periodo,
                              FormaPagamento formaPagamento) {
        return service.registar(pensaoId, periodo, formaPagamento);
    }

    public List<Pagamento> listar() {
        return service.listar();
    }

    public List<Pagamento> listarPorPensao(Long pensaoId) {
        return service.listarPorPensao(pensaoId);
    }

    public Optional<Pagamento> buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public Pagamento cancelar(Long id) {
        return service.cancelar(id);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public Pagamento obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Pagamento não encontrado: " + id));
    }

    public BigDecimal calcularTotalPago() {
        return service.calcularTotalPago();
    }

    public BigDecimal calcularTotalMes(Long pensionistaId, YearMonth mes) {
        return service.calcularTotalMes(pensionistaId, mes);
    }
}