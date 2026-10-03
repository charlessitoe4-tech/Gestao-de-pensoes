package dao;

import java.time.LocalDate;
import java.util.List;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Operacoes de persistencia para pagamentos mensais de pensoes. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate referencia,
                                      FormaPagamento formaPagamento);

    List<Pagamento> listarPorPensao(Long pensaoId);
}
