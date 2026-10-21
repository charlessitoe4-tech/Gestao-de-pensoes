package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.Historico;

/** Contrato de acesso a dados para histórico de operações. */
public interface HistoricoDAO {

    void criar(Historico historico);

    Optional<Historico> buscarPorId(Long id);

    List<Historico> listarTodos();

    List<Historico> listarPorEntidade(String entidade, Long entidadeId);

    List<Historico> listarPorUtilizador(Long utilizadorId);

    List<Historico> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    boolean remover(Long id);
}