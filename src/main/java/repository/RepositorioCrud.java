package repository;

import java.util.List;
import java.util.Optional;

/**
 * Define as operações CRUD genéricas para entidades identificadas por um ID.
 *
 * @param <T> tipo da entidade
 * @param <ID> tipo do identificador da entidade
 */
public interface RepositorioCrud<T, ID> {

    /** Guarda uma nova entidade. */
    void criar(T entidade);

    /** Procura uma entidade pelo identificador. */
    Optional<T> buscarPorId(ID id);

    /** Devolve uma cópia não modificável de todas as entidades guardadas. */
    List<T> listarTodos();

    /** Substitui a entidade existente pelo mesmo identificador. */
    void atualizar(ID id, T entidade);

    /** Remove uma entidade e indica se ela existia. */
    boolean remover(ID id);
}
