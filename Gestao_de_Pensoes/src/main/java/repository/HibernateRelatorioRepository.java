package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Relatorio;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do repositório CRUD de relatórios. */
public class HibernateRelatorioRepository
        implements RepositorioCrud<Relatorio, Long> {

    @Override
    public void criar(Relatorio relatorio) {
        Objects.requireNonNull(relatorio, "O relatório é obrigatório.");
        emTransacao(sessao -> {
            sessao.persist(relatorio);
            return null;
        });
    }

    @Override
    public Optional<Relatorio> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Relatorio.class, id));
        }
    }

    @Override
    public List<Relatorio> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Relatorio r order by r.dataGeracao desc", Relatorio.class)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Relatorio relatorio) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(relatorio, "O relatório é obrigatório.");
        if (!id.equals(relatorio.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do relatório.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Relatorio.class, id) == null) {
                throw new NoSuchElementException("Relatório não encontrado: " + id);
            }
            sessao.merge(relatorio);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Relatorio encontrado = sessao.find(Relatorio.class, id);
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