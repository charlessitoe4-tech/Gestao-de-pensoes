package dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Pagamento;
import model.Pensao;
import model.enums.EstadoPagamento;
import model.enums.FormaPagamento;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de pagamentos. */
public class HibernatePagamentoDAO implements PagamentoDAO {

    @Override
    public Pagamento registarPagamentoMensal(Long pensaoId, LocalDate dataReferencia,
                                             FormaPagamento formaPagamento) {
        Objects.requireNonNull(pensaoId, "O ID da pensão é obrigatório.");
        Objects.requireNonNull(dataReferencia, "A data de referência é obrigatória.");
        Objects.requireNonNull(formaPagamento, "A forma de pagamento é obrigatória.");

        return emTransacao(sessao -> {
            Pensao pensao = sessao.find(Pensao.class, pensaoId);
            if (pensao == null) {
                throw new NoSuchElementException("Pensão não encontrada: " + pensaoId);
            }

            // Verifica se já existe pagamento para o mesmo mês
            Long existentes = sessao.createQuery(
                            "select count(p) from Pagamento p where p.pensaoId = :pensaoId " +
                                    "and p.dataReferencia = :dataRef", Long.class)
                    .setParameter("pensaoId", pensaoId)
                    .setParameter("dataRef", dataReferencia)
                    .uniqueResult();
            if (existentes != null && existentes > 0) {
                throw new IllegalStateException(
                        "Já existe um pagamento registado para este mês de referência.");
            }

            Pagamento pagamento = new Pagamento();
            pagamento.setPensaoId(pensaoId);
            pagamento.setPensionistaId(pensao.getPensionistaId());
            pagamento.setValor(pensao.getValorMensal() != null
                    ? pensao.getValorMensal() : BigDecimal.ZERO);
            pagamento.setDataPagamento(LocalDate.now());
            pagamento.setDataReferencia(dataReferencia);
            pagamento.setEstado(EstadoPagamento.PAGO);
            pagamento.setFormaPagamento(formaPagamento);

            sessao.persist(pagamento);
            return pagamento;
        });
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Pagamento.class, id));
        }
    }

    @Override
    public List<Pagamento> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                    "from Pagamento p order by p.dataReferencia desc, p.id desc",
                    Pagamento.class).getResultList());
        }
    }

    @Override
    public List<Pagamento> listarPorPensao(Long pensaoId) {
        Objects.requireNonNull(pensaoId, "O ID da pensão é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Pagamento p where p.pensaoId = :pensaoId " +
                                    "order by p.dataReferencia desc", Pagamento.class)
                    .setParameter("pensaoId", pensaoId)
                    .getResultList());
        }
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Pagamento encontrado = sessao.find(Pagamento.class, id);
            if (encontrado == null) {
                return false;
            }
            sessao.remove(encontrado);
            return true;
        });
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