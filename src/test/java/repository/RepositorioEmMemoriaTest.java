package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class RepositorioEmMemoriaTest {

    private record Registo(Long id, String nome) {
    }

    @Test
    void executaOperacoesCrudEDevolveListaImutavel() {
        RepositorioEmMemoria<Registo, Long> repositorio =
                new RepositorioEmMemoria<>(Registo::id);
        Registo inicial = new Registo(1L, "Ana");
        repositorio.criar(inicial);

        assertEquals(inicial, repositorio.buscarPorId(1L).orElseThrow());
        assertThrows(UnsupportedOperationException.class,
                () -> repositorio.listarTodos().clear());

        Registo atualizado = new Registo(1L, "Maria");
        repositorio.atualizar(1L, atualizado);
        assertEquals(atualizado, repositorio.buscarPorId(1L).orElseThrow());

        assertTrue(repositorio.remover(1L));
        assertFalse(repositorio.remover(1L));
        assertThrows(NoSuchElementException.class,
                () -> repositorio.atualizar(1L, atualizado));
    }
}
