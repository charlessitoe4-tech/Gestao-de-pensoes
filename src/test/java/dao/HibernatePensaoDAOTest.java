package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import model.Pagamento;
import model.Pensao;
import model.PensaoVelhice;
import model.Pensionista;
import model.enums.EstadoPensao;
import model.enums.FormaPagamento;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.HibernateUtil;

class HibernatePensaoDAOTest {

    @AfterEach
    void encerrarHibernate() {
        HibernateUtil.encerrar();
        System.clearProperty("GESTAO_PENSOES_DB_URL");
    }

    @Test
    void persistePensaoPolimorficaEPagamentoUnicoPorMes() {
        System.setProperty("GESTAO_PENSOES_DB_URL",
                "jdbc:h2:mem:pensao_" + UUID.randomUUID().toString().replace("-", "")
                + ";DB_CLOSE_DELAY=-1");

        Pensionista pensionista = criarPensionista();
        Pensao pensao = new PensaoVelhice();
        pensao.setNumeroProcesso("PROC-TESTE-1");
        pensao.setPensionistaId(pensionista.getId());
        pensao.setTipo(model.enums.TipoPensao.VELHICE);
        pensao.setEstado(EstadoPensao.ATIVA);
        pensao.setValorMensal(new BigDecimal("2500.00"));
        pensao.setDataInicio(LocalDate.of(2026, 1, 1));

        HibernatePensaoDAO pensoes = new HibernatePensaoDAO();
        pensoes.criar(pensao);
        Pensao recuperada = pensoes.buscarPorId(pensao.getId()).orElseThrow();

        assertEquals(PensaoVelhice.class, recuperada.getClass());
        assertEquals("PROC-TESTE-1", recuperada.getNumeroProcesso());

        HibernatePagamentoDAO pagamentos = new HibernatePagamentoDAO();
        Pagamento registado = pagamentos.registarPagamentoMensal(
                pensao.getId(), YearMonth.now().atDay(1), FormaPagamento.MOVEL);

        assertEquals(pensao.getId(), registado.getPensaoId());
        assertEquals(new BigDecimal("2500.00"), registado.getValor());
        assertEquals(1, pagamentos.listarPorPensao(pensao.getId()).size());
        assertThrows(IllegalStateException.class,
                () -> pagamentos.registarPagamentoMensal(
                        pensao.getId(), YearMonth.now().atDay(1), FormaPagamento.MOVEL));
    }

    private Pensionista criarPensionista() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transacao = sessao.beginTransaction();
            Pensionista pensionista = new Pensionista();
            pensionista.setNome("Pensionista de teste");
            pensionista.setNumeroBI("BI-" + UUID.randomUUID());
            pensionista.setAtivo(true);
            sessao.persist(pensionista);
            transacao.commit();
            return pensionista;
        }
    }
}
