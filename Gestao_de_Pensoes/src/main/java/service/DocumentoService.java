package service;

import dao.DocumentoDAO;
import dao.HibernateDocumentoDAO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import model.Documento;

/** Regras de negócio para os documentos associados a beneficiários. */
public class DocumentoService {

    private final DocumentoDAO repositorio;

    public DocumentoService() {
        this(new HibernateDocumentoDAO());
    }

    public DocumentoService(DocumentoDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Documento guardar(Documento documento) {
        Objects.requireNonNull(documento, "O documento é obrigatório.");
        if (documento.getTipo() == null) {
            throw new IllegalArgumentException("O tipo de documento é obrigatório.");
        }
        if (documento.getPessoaId() == null) {
            throw new IllegalArgumentException("Selecione o beneficiário do documento.");
        }
        if (documento.getCaminhoFicheiro() == null || documento.getCaminhoFicheiro().isBlank()) {
            throw new IllegalArgumentException("Selecione o ficheiro do documento.");
        }
        if (documento.getId() == null) {
            repositorio.criar(documento);
        } else {
            repositorio.atualizar(documento.getId(), documento);
        }
        return documento;
    }

    public List<Documento> listar() {
        return repositorio.listarTodos();
    }

    public List<Documento> listarPorPessoa(Long pessoaId) {
        Objects.requireNonNull(pessoaId, "O beneficiário é obrigatório.");
        return repositorio.listarPorPessoa(pessoaId);
    }

    public Optional<Documento> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }
}
