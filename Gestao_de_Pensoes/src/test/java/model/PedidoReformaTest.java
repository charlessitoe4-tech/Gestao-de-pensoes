package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;
import model.enums.EstadoPedidoReforma;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.HibernateUtil;

class PedidoReformaTest {

    @AfterEach
    void encerrarHibernate() {
        HibernateUtil.encerrar();
        System.clearProperty("GESTAO_PENSOES_DB_URL");
    }

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

    @Test
    void persistePedidoEEstadoComHibernate() {
        System.setProperty("GESTAO_PENSOES_DB_URL",
                "jdbc:h2:mem:pedido_reforma_"
                + UUID.randomUUID().toString().replace("-", "") + ";DB_CLOSE_DELAY=-1");
        PedidoReforma pedido = pedidoValido();
        pedido.solicitar();

        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transacao = sessao.beginTransaction();
            sessao.persist(pedido);
            transacao.commit();
        }

        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            PedidoReforma recuperado = sessao.find(PedidoReforma.class, pedido.getId());
            assertEquals("REF-2026-001", recuperado.getNumeroProcesso());
            assertEquals(EstadoPedidoReforma.PENDENTE, recuperado.getEstado());
            assertTrue(recuperado.verificar());
        }
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
