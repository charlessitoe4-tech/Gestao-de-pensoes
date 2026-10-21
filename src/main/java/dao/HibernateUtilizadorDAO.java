package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Utilizador;
import model.Perfil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/**
 * Representa HibernateUtilizadorDAO, componente do sistema de gestão de pensões.
 * Mantém a responsabilidade definida pela camada/pacote em que está organizado.
 */
public class HibernateUtilizadorDAO implements UtilizadorDAO {

    @Override
    public void criar(Utilizador utilizador) {
        Objects.requireNonNull(utilizador, "O utilizador é obrigatório.");
        emTransacao(sessao -> {
            // O perfil pode ter vindo de outra sessão Hibernate; reassociá-lo à sessão atual.
            if (utilizador.getPerfil() != null && utilizador.getPerfil().getId() != null) {
                utilizador.setPerfil(sessao.getReference(Perfil.class, utilizador.getPerfil().getId()));
            }
            sessao.persist(utilizador);
            return null;
        });
    }

    @Override
    public Optional<Utilizador> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Utilizador.class, id));
        }
    }

    @Override
    public Optional<Utilizador> buscarPorUsername(String username) {
        Objects.requireNonNull(username, "O username é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from Utilizador u where u.username = :username", Utilizador.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Utilizador> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Utilizador u order by u.id", Utilizador.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Utilizador utilizador) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(utilizador, "O utilizador é obrigatório.");
        if (!id.equals(utilizador.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do utilizador.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Utilizador.class, id) == null) {
                throw new NoSuchElementException("Utilizador não encontrado: " + id);
            }
            if (utilizador.getPerfil() != null && utilizador.getPerfil().getId() != null) {
                utilizador.setPerfil(sessao.getReference(Perfil.class, utilizador.getPerfil().getId()));
            }
            sessao.merge(utilizador);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Utilizador encontrado = sessao.find(Utilizador.class, id);
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