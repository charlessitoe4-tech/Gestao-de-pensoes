package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Pensao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de pensões. */
public class HibernatePensaoDAO implements PensaoDAO {

    @Override
    public void criar(Pensao pensao) {
        Objects.requireNonNull(pensao, "A pensão é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(pensao);
            return null;
        });
    }

    @Override
    public Optional<Pensao> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Pensao.class, id));
        }
    }

    @Override
    public List<Pensao> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Pensao pensao order by pensao.id", Pensao.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Pensao pensao) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(pensao, "A pensão é obrigatória.");
        if (!id.equals(pensao.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da pensão.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Pensao.class, id) == null) {
                throw new NoSuchElementException("Pensão não encontrada: " + id);
            }
            sessao.merge(pensao);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Pensao encontrada = sessao.find(Pensao.class, id);
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