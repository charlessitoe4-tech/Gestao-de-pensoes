package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Perfil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de perfis. */
public class HibernatePerfilDAO implements PerfilDAO {

    @Override
    public void criar(Perfil perfil) {
        Objects.requireNonNull(perfil, "O perfil é obrigatório.");
        emTransacao(sessao -> {
            sessao.persist(perfil);
            return null;
        });
    }

    @Override
    public Optional<Perfil> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Perfil.class, id));
        }
    }

    @Override
    public Optional<Perfil> buscarPorNome(String nome) {
        Objects.requireNonNull(nome, "O nome é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from Perfil p where p.nome = :nome", Perfil.class)
                    .setParameter("nome", nome)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Perfil> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Perfil p order by p.nome", Perfil.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Perfil perfil) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(perfil, "O perfil é obrigatório.");
        if (!id.equals(perfil.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do perfil.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Perfil.class, id) == null) {
                throw new NoSuchElementException("Perfil não encontrado: " + id);
            }
            sessao.merge(perfil);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Perfil encontrado = sessao.find(Perfil.class, id);
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