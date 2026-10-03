package dao;

import java.util.List;
import java.util.Optional;
import model.Documento;
import model.enums.TipoDocumento;

/** Contrato de acesso a dados para documentos. */
public interface DocumentoDAO {

    void criar(Documento documento);

    Optional<Documento> buscarPorId(Long id);

    List<Documento> listarTodos();

    List<Documento> listarPorPessoa(Long pessoaId);

    List<Documento> listarPorPessoaETipo(Long pessoaId, TipoDocumento tipo);

    void atualizar(Long id, Documento documento);

    boolean remover(Long id);
}