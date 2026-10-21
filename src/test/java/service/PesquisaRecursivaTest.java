package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PesquisaRecursivaTest {

    private record Registo(long id) {
    }

    @Test
    void encontraElementoEmListaOrdenada() {
        List<Registo> registos = List.of(new Registo(2), new Registo(7), new Registo(11));

        assertEquals(7, PesquisaRecursiva.buscarPorId(registos, 7, Registo::id)
                .orElseThrow().id());
    }

    @Test
    void devolveVazioQuandoIdNaoExiste() {
        List<Registo> registos = List.of(new Registo(2), new Registo(7), new Registo(11));

        assertTrue(PesquisaRecursiva.buscarPorId(registos, 5, Registo::id).isEmpty());
    }

    @Test
    void aceitaListaVazia() {
        assertTrue(PesquisaRecursiva.buscarPorId(List.of(), 1, Registo::id).isEmpty());
    }
}
