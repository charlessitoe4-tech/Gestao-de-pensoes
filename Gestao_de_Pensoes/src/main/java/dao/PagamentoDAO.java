package dao;

import java.time.LocalDate;
import java.util.List;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Operações de persistência para pagamentos mensais de pensões. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate referencia,
                                      FormaPagamento formaPagamento);

    List<Pagamento> listarPorPensao(Long pensaoId);
import java.time.LocalDate;
import java.util.List;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Operações de persistência para pagamentos mensais de pensões. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate referencia,
                                      FormaPagamento formaPagamento);

    List<Pagamento> listarPorPensao(Long pensaoId);
}
