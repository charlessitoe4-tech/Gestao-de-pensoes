package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Pessoa;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/**
 * Implementação Hibernate do DAO de pessoas.
 *
 * <p>Como {@code Pessoa} é {@code @MappedSuperclass}, não é possível fazer
 * consultas diretas a esta classe via HQL. Este DAO deve ser usado apenas
 * como base comum — as subclasses concretas devem ter os seus próprios DAOs.</p>
 */
public abstract class HibernatePessoaDAO<T extends Pessoa> implements PessoaDAO {

    private final Class<T> tipo;

    protected HibernatePessoaDAO(Class<T> tipo) {
        this.tipo = Objects.requireNonNull(tipo, "O tipo da entidade é obrigatório.");
    }

    @Override
    public void criar(Pessoa pessoa) {
        Objects.requireNonNull(pessoa, "A pessoa é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(pessoa);
            return null;
        });
    }

    @Override
    public Optional<Pessoa> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(tipo, id));
        }
    }

    @Override
    public Optional<Pessoa> buscarPorBI(String numeroBI) {
        Objects.requireNonNull(numeroBI, "O número de BI é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from " + tipo.getSimpleName() + " p where p.numeroBI = :bi", tipo)
                    .setParameter("bi", numeroBI)
                    .uniqueResultOptional()
                    .map(Pessoa.class::cast);
        }
    }

    @Override
    public Optional<Pessoa> buscarPorNuit(String nuit) {
        Objects.requireNonNull(nuit, "O NUIT é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from " + tipo.getSimpleName() + " p where p.nuit = :nuit", tipo)
                    .setParameter("nuit", nuit)
                    .uniqueResultOptional()
                    .map(Pessoa.class::cast);
        }
    }

    @Override
    public List<Pessoa> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from " + tipo.getSimpleName() + " p order by p.id", tipo)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Pessoa pessoa) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(pessoa, "A pessoa é obrigatória.");
        if (!id.equals(pessoa.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da pessoa.");
        }
        emTransacao(sessao -> {
            if (sessao.find(tipo, id) == null) {
                throw new NoSuchElementException("Pessoa não encontrada: " + id);
            }
            sessao.merge(pessoa);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Pessoa encontrada = sessao.find(tipo, id);
            if (encontrada == null) {
                return false;
            }
            sessao.remove(encontrada);
            return true;
        });
    }

    private <R> R emTransacao(Function<Session, R> operacao) {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transacao = sessao.beginTransaction();
            try {
                R resultado = operacao.apply(sessao);
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