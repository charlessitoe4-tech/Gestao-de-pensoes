package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Pensionista;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de pensionistas. */
public class HibernatePensionistaDAO implements PensionistaDAO {

    @Override
    public void criar(Pensionista pensionista) {
        Objects.requireNonNull(pensionista, "O pensionista é obrigatório.");
        emTransacao(sessao -> {
            sessao.persist(pensionista);
            return null;
        });
    }

    @Override
    public Optional<Pensionista> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Pensionista.class, id));
        }
    }

    @Override
    public Optional<Pensionista> buscarPorNumero(String numeroPensionista) {
        Objects.requireNonNull(numeroPensionista, "O número é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from Pensionista p where p.numeroPensionista = :numero",
                            Pensionista.class)
                    .setParameter("numero", numeroPensionista)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Pensionista> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Pensionista p order by p.id", Pensionista.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Pensionista pensionista) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(pensionista, "O pensionista é obrigatório.");
        if (!id.equals(pensionista.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do pensionista.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Pensionista.class, id) == null) {
                throw new NoSuchElementException("Pensionista não encontrado: " + id);
            }
            sessao.merge(pensionista);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Pensionista encontrado = sessao.find(Pensionista.class, id);
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