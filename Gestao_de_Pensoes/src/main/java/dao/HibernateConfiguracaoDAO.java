package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Configuracao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de configurações. */
public class HibernateConfiguracaoDAO implements ConfiguracaoDAO {

    @Override
    public void criar(Configuracao configuracao) {
        Objects.requireNonNull(configuracao, "A configuração é obrigatória.");
        emTransacao(sessao -> {
            sessao.persist(configuracao);
            return null;
        });
    }

    @Override
    public Optional<Configuracao> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Configuracao.class, id));
        }
    }

    @Override
    public Optional<Configuracao> buscarPorChave(String chave) {
        Objects.requireNonNull(chave, "A chave é obrigatória.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return sessao.createQuery(
                            "from Configuracao c where c.chave = :chave", Configuracao.class)
                    .setParameter("chave", chave)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Configuracao> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Configuracao c order by c.grupo, c.chave", Configuracao.class)
                    .getResultList());
        }
    }

    @Override
    public List<Configuracao> listarPorGrupo(String grupo) {
        Objects.requireNonNull(grupo, "O grupo é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Configuracao c where c.grupo = :grupo order by c.chave",
                            Configuracao.class)
                    .setParameter("grupo", grupo)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Configuracao configuracao) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(configuracao, "A configuração é obrigatória.");
        if (!id.equals(configuracao.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao da configuração.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Configuracao.class, id) == null) {
                throw new NoSuchElementException("Configuração não encontrada: " + id);
            }
            sessao.merge(configuracao);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Configuracao encontrada = sessao.find(Configuracao.class, id);
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