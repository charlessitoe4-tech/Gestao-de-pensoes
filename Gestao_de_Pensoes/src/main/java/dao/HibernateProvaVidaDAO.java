package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.ProvaVida;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

public class HibernateProvaVidaDAO implements ProvaVidaDAO {

    @Override
    public void criar(ProvaVida provaVida) {
        Objects.requireNonNull(provaVida, "A prova de vida é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(provaVida);
            return null;
        });
    }

    @Override
    public Optional<ProvaVida> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(ProvaVida.class, id));
        }
    }

    @Override
    public List<ProvaVida> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                    "from ProvaVida pv order by pv.dataRealizacao desc",
                    ProvaVida.class).getResultList());
        }
    }

    @Override
    public List<ProvaVida> listarPorPensionista(Long pensionistaId) {
        Objects.requireNonNull(pensionistaId, "O ID do pensionista é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from ProvaVida pv where pv.pensionistaId = :pensionistaId " +
                                    "order by pv.dataRealizacao desc", ProvaVida.class)
                    .setParameter("pensionistaId", pensionistaId)
                    .getResultList());
        }
    }

    @Override
    public Optional<ProvaVida> buscarMaisRecente(Long pensionistaId) {
        Objects.requireNonNull(pensionistaId, "O ID do pensionista é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from ProvaVida pv where pv.pensionistaId = :pensionistaId " +
                                    "order by pv.dataRealizacao desc", ProvaVida.class)
                    .setParameter("pensionistaId", pensionistaId)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        }
    }

    @Override
    public void atualizar(Long id, ProvaVida provaVida) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(provaVida, "A prova de vida é obrigatória.");
        if (!id.equals(provaVida.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da prova de vida.");
        }
        emTransacao(sessao -> {
            if (sessao.find(ProvaVida.class, id) == null) {
                throw new NoSuchElementException("Prova de vida não encontrada: " + id);
            }
            sessao.merge(provaVida);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            ProvaVida encontrada = sessao.find(ProvaVida.class, id);
            if (encontrada == null) {
                return false;
            }
            sessao.remove(encontrada);
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