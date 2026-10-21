package dao;

import java.util.List;
import java.util.Optional;
import model.ProvaVida;

/**
 * Representa ProvaVidaDAO, componente do sistema de gestão de pensões.
 * Mantém a responsabilidade definida pela camada/pacote em que está organizado.
 */
public interface ProvaVidaDAO {

    void criar(ProvaVida provaVida);

    Optional<ProvaVida> buscarPorId(Long id);

    List<ProvaVida> listarTodos();

    List<ProvaVida> listarPorPensionista(Long pensionistaId);

    Optional<ProvaVida> buscarMaisRecente(Long pensionistaId);

    void atualizar(Long id, ProvaVida provaVida);

    boolean remover(Long id);
}