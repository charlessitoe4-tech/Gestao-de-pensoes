package repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import model.Pagamento;
import model.Pensao;
import model.enums.EstadoPagamento;
import model.enums.EstadoPensao;
import model.enums.FormaPagamento;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Persiste pagamentos mensais numa transação e impede duplicados por período. */
public class HibernatePagamentoPensaoRepository implements PagamentoPensaoRepository {

    @Override
    public Pagamento registarPagamentoMensal(
            Long pensaoId, LocalDate referencia, FormaPagamento formaPagamento) {
        Objects.requireNonNull(pensaoId, "A pensão é obrigatória.");
        Objects.requireNonNull(referencia, "O período de referência é obrigatório.");
        Objects.requireNonNull(formaPagamento, "A forma de pagamento é obrigatória.");
        return emTransacao(sessao -> {
            Pensao pensao = sessao.find(Pensao.class, pensaoId);
            if (pensao == null) {
                throw new IllegalArgumentException("Pensão não encontrada.");
            }
            if (pensao.getEstado() != EstadoPensao.ATIVA) {
                throw new IllegalStateException("Só é possível pagar uma pensão ativa.");
            }
            LocalDate fimPeriodo = referencia.withDayOfMonth(referencia.lengthOfMonth());
            if (pensao.getDataInicio().isAfter(fimPeriodo)
                    || pensao.getDataFim() != null && pensao.getDataFim().isBefore(referencia)) {
                throw new IllegalStateException(
                        "A pensão não esteve em vigor durante o período selecionado.");
            }
            Long pagamentosExistentes = sessao.createQuery(
                    "select count(pagamento) from Pagamento pagamento "
                    + "where pagamento.pensaoId = :pensaoId "
                    + "and pagamento.dataReferencia = :referencia",
                    Long.class)
                    .setParameter("pensaoId", pensaoId)
                    .setParameter("referencia", referencia)
                    .getSingleResult();
            if (pagamentosExistentes > 0) {
                throw new IllegalStateException(
                        "Já existe um pagamento para esta pensão nesse mês.");
            }

            Pagamento pagamento = new Pagamento();
            pagamento.setPensaoId(pensaoId);
            pagamento.setPensionistaId(pensao.getPensionistaId());
            pagamento.setValor(pensao.getValorMensal());
            pagamento.setDataPagamento(LocalDate.now());
            pagamento.setDataReferencia(referencia);
            pagamento.setEstado(EstadoPagamento.PAGO);
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setReferencia(pensao.getNumeroProcesso() + "-"
                    + referencia.getYear() + String.format("%02d", referencia.getMonthValue()));
            pagamento.setObservacoes("Pagamento mensal registado na gestão da pensão.");
            sessao.persist(pagamento);
            return pagamento;
        });
    }

    @Override
    public List<Pagamento> listarPorPensao(Long pensaoId) {
        Objects.requireNonNull(pensaoId, "A pensão é obrigatória.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                    "from Pagamento pagamento where pagamento.pensaoId = :pensaoId "
                    + "order by pagamento.dataReferencia desc",
                    Pagamento.class)
                    .setParameter("pensaoId", pensaoId)
                    .getResultList());
        }
    }

    private <T> T emTransacao(Function<Session, T> operacao) {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transacao = sessao.beginTransaction();
            try {
                T resultado = operacao.apply(sessao);
                transacao.commit();
                return resultado;
            } catch (RuntimeException ex) {
                if (transacao.isActive()) {
                    transacao.rollback();
                }
                throw ex;
            }
        }
    }
}
