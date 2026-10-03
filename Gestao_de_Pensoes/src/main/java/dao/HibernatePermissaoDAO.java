package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Permissao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de permissões. */
public class HibernatePermissaoDAO implements PermissaoDAO {

    @Override
    public void criar(Permissao permissao) {
        Objects.requireNonNull(permissao, "A permissão é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(permissao);
            return null;
        });
    }

    @Override
    public Optional<Permissao> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Permissao.class, id));
        }
    }

    @Override
    public Optional<Permissao> buscarPorCodigo(String codigo) {
        Objects.requireNonNull(codigo, "O código é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from Permissao p where p.codigo = :codigo", Permissao.class)
                    .setParameter("codigo", codigo)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Permissao> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Permissao p order by p.modulo, p.codigo", Permissao.class)
                    .getResultList());
        }
    }

    @Override
    public List<Permissao> listarPorModulo(String modulo) {
        Objects.requireNonNull(modulo, "O módulo é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Permissao p where p.modulo = :modulo order by p.codigo",
                            Permissao.class)
                    .setParameter("modulo", modulo)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Permissao permissao) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(permissao, "A permissão é obrigatória.");
        if (!id.equals(permissao.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da permissão.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Permissao.class, id) == null) {
                throw new NoSuchElementException("Permissão não encontrada: " + id);
            }
            sessao.merge(permissao);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Permissao encontrada = sessao.find(Permissao.class, id);
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