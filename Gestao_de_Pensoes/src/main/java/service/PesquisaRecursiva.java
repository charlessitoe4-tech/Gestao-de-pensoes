package service;

import java.util.List;
import java.util.Optional;
import java.util.function.ToLongFunction;

/** Pesquisa recursivamente um ID numa lista ordenada pelo mesmo ID. */
public final class PesquisaRecursiva {

    private PesquisaRecursiva() {
    }

    /**
     * Localiza o ID com pesquisa binária recursiva.
     *
     * @param elementos lista ordenada crescentemente pelo ID
     * @param id ID procurado
     * @param extrairId função que extrai o ID de cada elemento
     * @param <T> tipo dos elementos
     * @return elemento encontrado, ou vazio se o ID não existir
     */
    public static <T> Optional<T> buscarPorId(
            List<T> elementos, long id, ToLongFunction<T> extrairId) {
        return buscar(elementos, id, extrairId, 0, elementos.size() - 1);
    }

    private static <T> Optional<T> buscar(
            List<T> elementos, long id, ToLongFunction<T> extrairId, int inicio, int fim) {
        if (inicio > fim) {
            return Optional.empty();
        }
        int meio = inicio + (fim - inicio) / 2;
        long idAtual = extrairId.applyAsLong(elementos.get(meio));
        if (idAtual == id) {
            return Optional.of(elementos.get(meio));
        }
        if (id < idAtual) {
            return buscar(elementos, id, extrairId, inicio, meio - 1);
        }
        return buscar(elementos, id, extrairId, meio + 1, fim);
    }
}
