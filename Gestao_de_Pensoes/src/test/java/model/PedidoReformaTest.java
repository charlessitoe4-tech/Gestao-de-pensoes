package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import model.enums.EstadoPedidoReforma;
import org.junit.jupiter.api.Test;

class PedidoReformaTest {

    @Test
    void solicitaVerificaEAceitaPedido() {
        PedidoReforma pedido = pedidoValido();

        assertFalse(pedido.verificar());
        pedido.solicitar();

        assertEquals(LocalDate.now(), pedido.getDataSolicitacao());
        assertEquals(EstadoPedidoReforma.PENDENTE, pedido.getEstado());
        assertTrue(pedido.verificar());

        pedido.aceitar();

        assertEquals(EstadoPedidoReforma.ACEITE, pedido.getEstado());
        assertEquals(LocalDate.now(), pedido.getDataDecisao());
        assertFalse(pedido.verificar());
        assertThrows(IllegalStateException.class, pedido::aceitar);
    }

    @Test
    void rejeitaPedidoPendenteComMotivoObrigatorio() {
        PedidoReforma pedido = pedidoValido();
        pedido.solicitar();

        assertThrows(IllegalArgumentException.class, () -> pedido.rejeitar(" "));
        pedido.rejeitar("  Dados incompletos  ");

        assertEquals(EstadoPedidoReforma.REJEITADO, pedido.getEstado());
        assertEquals("Dados incompletos", pedido.getMotivoRejeicao());
        assertEquals(LocalDate.now(), pedido.getDataDecisao());
    }

    @Test
    void naoSubmetePedidoComDadosInvalidos() {
        PedidoReforma pedido = new PedidoReforma();

        assertThrows(IllegalArgumentException.class, pedido::solicitar);
        assertFalse(pedido.verificar());
    }

    private PedidoReforma pedidoValido() {
        PedidoReforma pedido = new PedidoReforma();
        pedido.setNumeroProcesso("REF-2026-001");
        pedido.setPensionistaId(1L);
        pedido.setAnosContribuicao(20);
        pedido.setObservacoes("Pedido de reforma por idade.");
        return pedido;
    }
}
