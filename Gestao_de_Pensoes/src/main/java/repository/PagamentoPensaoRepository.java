package repository;

import java.time.LocalDate;
import java.util.List;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Operações de persistência específicas para pagamentos de pensões. */
public interface PagamentoPensaoRepository {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate referencia,
                                      FormaPagamento formaPagamento);

    List<Pagamento> listarPorPensao(Long pensaoId);
}
