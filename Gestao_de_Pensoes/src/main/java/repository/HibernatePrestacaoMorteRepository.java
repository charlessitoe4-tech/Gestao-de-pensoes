package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.PrestacaoMorte;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do repositório CRUD de prestações por morte. */
public class HibernatePrestacaoMorteRepository
        implements RepositorioCrud<PrestacaoMorte, Long> {

    @Override
    public void criar(PrestacaoMorte prestacao) {
        Objects.requireNonNull(prestacao, "A prestação é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(prestacao);
            return null;
        });
    }

    @Override
    public Optional<PrestacaoMorte> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(PrestacaoMorte.class, id));
        }
    }

    @Override
    public List<PrestacaoMorte> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from PrestacaoMorte p order by p.id", PrestacaoMorte.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, PrestacaoMorte prestacao) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(prestacao, "A prestação é obrigatória.");
        if (!id.equals(prestacao.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da prestação.");
        }
        emTransacao(sessao -> {
            if (sessao.find(PrestacaoMorte.class, id) == null) {
                throw new NoSuchElementException("Prestação não encontrada: " + id);
            }
            sessao.merge(prestacao);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            PrestacaoMorte encontrada = sessao.find(PrestacaoMorte.class, id);
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