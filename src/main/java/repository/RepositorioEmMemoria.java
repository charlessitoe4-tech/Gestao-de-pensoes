package repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementação genérica e em memória das operações CRUD.
 *
 * <p>Os dados existem apenas durante a execução da aplicação. O extrator
 * fornecido no construtor permite usar modelos existentes sem os alterar.</p>
 *
 * @param <T> tipo da entidade
 * @param <ID> tipo do identificador da entidade
 */
public class RepositorioEmMemoria<T, ID> implements RepositorioCrud<T, ID> {

    private final Map<ID, T> entidades = new LinkedHashMap<>();
    private final Function<T, ID> extrairId;

    /**
     * Cria um repositório para entidades e respetivos identificadores.
     *
     * @param extrairId função que obtém o identificador da entidade
     */
    public RepositorioEmMemoria(Function<T, ID> extrairId) {
        this.extrairId = Objects.requireNonNull(extrairId, "O extrator de ID é obrigatório.");
    }

    @Override
    public synchronized void criar(T entidade) {
        Objects.requireNonNull(entidade, "A entidade é obrigatória.");
        ID id = obterId(entidade);
        if (entidades.containsKey(id)) {
            throw new IllegalArgumentException("Já existe uma entidade com o ID " + id + ".");
        }
        entidades.put(id, entidade);
    }

    @Override
    public synchronized Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(entidades.get(Objects.requireNonNull(id, "O ID é obrigatório.")));
    }

    @Override
    public synchronized List<T> listarTodos() {
        return List.copyOf(new ArrayList<>(entidades.values()));
    }

    @Override
    public synchronized void atualizar(ID id, T entidade) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(entidade, "A entidade é obrigatória.");
        if (!entidades.containsKey(id)) {
            throw new NoSuchElementException("Não existe entidade com o ID " + id + ".");
        }
        if (!id.equals(obterId(entidade))) {
            throw new IllegalArgumentException("O ID da entidade deve corresponder ao ID indicado.");
        }
        entidades.put(id, entidade);
    }

    @Override
    public synchronized boolean remover(ID id) {
        return entidades.remove(Objects.requireNonNull(id, "O ID é obrigatório.")) != null;
    }

    private ID obterId(T entidade) {
        ID id = extrairId.apply(entidade);
        return Objects.requireNonNull(id, "O ID da entidade é obrigatório.");
    }
}
