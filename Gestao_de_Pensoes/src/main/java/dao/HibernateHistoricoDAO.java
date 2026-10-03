package dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Historico;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de histórico. */
public class HibernateHistoricoDAO implements HistoricoDAO {

    @Override
    public void criar(Historico historico) {
        Objects.requireNonNull(historico, "O histórico é obrigatório.");
        if (historico.getDataHora() == null) {
            historico.setDataHora(LocalDateTime.now());
        }
        emTransacao(sessao -> {
            sessao.persist(historico);
            return null;
        });
    }

    @Override
    public Optional<Historico> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Historico.class, id));
        }
    }

    @Override
    public List<Historico> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Historico h order by h.dataHora desc", Historico.class)
                    .getResultList());
        }
    }

    @Override
    public List<Historico> listarPorEntidade(String entidade, Long entidadeId) {
        Objects.requireNonNull(entidade, "A entidade é obrigatória.");
        Objects.requireNonNull(entidadeId, "O ID da entidade é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Historico h where h.entidade = :entidade " +
                                    "and h.entidadeId = :entidadeId order by h.dataHora desc",
                            Historico.class)
                    .setParameter("entidade", entidade)
                    .setParameter("entidadeId", entidadeId)
                    .getResultList());
        }
    }

    @Override
    public List<Historico> listarPorUtilizador(Long utilizadorId) {
        Objects.requireNonNull(utilizadorId, "O ID do utilizador é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Historico h where h.utilizadorId = :utilizadorId " +
                                    "order by h.dataHora desc", Historico.class)
                    .setParameter("utilizadorId", utilizadorId)
                    .getResultList());
        }
    }

    @Override
    public List<Historico> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Objects.requireNonNull(inicio, "A data de início é obrigatória.");
        Objects.requireNonNull(fim, "A data de fim é obrigatória.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Historico h where h.dataHora between :inicio and :fim " +
                                    "order by h.dataHora desc", Historico.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fim", fim)
                    .getResultList());
        }
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Historico encontrado = sessao.find(Historico.class, id);
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