package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import model.Pagamento;
import model.enums.FormaPagamento;

<<<<<<< HEAD
/** Contrato de acesso a dados para pagamentos de pensões. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate dataReferencia,
                                      FormaPagamento formaPagamento);

    Optional<Pagamento> buscarPorId(Long id);

    List<Pagamento> listarTodos();
=======
/** Operacoes de persistencia para pagamentos mensais de pensoes. */
public interface PagamentoDAO {

    Pagamento registarPagamentoMensal(Long pensaoId, LocalDate referencia,
                                      FormaPagamento formaPagamento);
>>>>>>> 0a2bc51333b2b1ccf5584282a4d546c55c5ef3c6

    List<Pagamento> listarPorPensao(Long pensaoId);

    boolean remover(Long id);
}