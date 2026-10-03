package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import model.Pagamento;
import model.enums.FormaPagamento;

/** Contrato de acesso a dados para pagamentos de pensões. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate dataReferencia,
                                      FormaPagamento formaPagamento);

    Optional<Pagamento> buscarPorId(Long id);

    List<Pagamento> listarTodos();

    List<Pagamento> listarPorPensao(Long pensaoId);

    boolean remover(Long id);
}