package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Beneficiario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do repositório CRUD de beneficiários. */
public class HibernateBeneficiarioRepository implements RepositorioCrud<Beneficiario, Long> {

    @Override
    public void criar(Beneficiario beneficiario) {
        Objects.requireNonNull(beneficiario, "O beneficiário é obrigatório.");
        emTransacao(sessao -> {
            sessao.persist(beneficiario);
            return null;
        });
    }

    @Override
    public Optional<Beneficiario> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Beneficiario.class, id));
        }
    }

    @Override
    public List<Beneficiario> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                    "from Beneficiario beneficiario order by beneficiario.id", Beneficiario.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Beneficiario beneficiario) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(beneficiario, "O beneficiário é obrigatório.");
        if (!id.equals(beneficiario.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do beneficiário.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Beneficiario.class, id) == null) {
                throw new NoSuchElementException("Beneficiário não encontrado: " + id);
            }
            sessao.merge(beneficiario);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Beneficiario encontrado = sessao.find(Beneficiario.class, id);
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
