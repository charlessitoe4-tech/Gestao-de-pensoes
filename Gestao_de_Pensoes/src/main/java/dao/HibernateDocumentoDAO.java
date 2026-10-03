package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Documento;
import model.enums.TipoDocumento;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de documentos. */
public class HibernateDocumentoDAO implements DocumentoDAO {

    @Override
    public void criar(Documento documento) {
        Objects.requireNonNull(documento, "O documento é obrigatório.");
        emTransacao(sessao -> {
            sessao.persist(documento);
            return null;
        });
    }

    @Override
    public Optional<Documento> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(sessao.find(Documento.class, id));
        }
    }

    @Override
    public List<Documento> listarTodos() {
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Documento d order by d.id", Documento.class)
                    .getResultList());
        }
    }

    @Override
    public List<Documento> listarPorPessoa(Long pessoaId) {
        Objects.requireNonNull(pessoaId, "O ID da pessoa é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Documento d where d.pessoaId = :pessoaId order by d.id",
                            Documento.class)
                    .setParameter("pessoaId", pessoaId)
                    .getResultList());
        }
    }

    @Override
    public List<Documento> listarPorPessoaETipo(Long pessoaId, TipoDocumento tipo) {
        Objects.requireNonNull(pessoaId, "O ID da pessoa é obrigatório.");
        Objects.requireNonNull(tipo, "O tipo é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Documento d where d.pessoaId = :pessoaId and d.tipo = :tipo",
                            Documento.class)
                    .setParameter("pessoaId", pessoaId)
                    .setParameter("tipo", tipo)
                    .getResultList());
        }
    }

    @Override
    public void atualizar(Long id, Documento documento) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Objects.requireNonNull(documento, "O documento é obrigatório.");
        if (!id.equals(documento.getId())) {
            throw new IllegalArgumentException("O ID indicado deve corresponder ao do documento.");
        }
        emTransacao(sessao -> {
            if (sessao.find(Documento.class, id) == null) {
                throw new NoSuchElementException("Documento não encontrado: " + id);
            }
            sessao.merge(documento);
            return null;
        });
    }

    @Override
    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return emTransacao(sessao -> {
            Documento encontrado = sessao.find(Documento.class, id);
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