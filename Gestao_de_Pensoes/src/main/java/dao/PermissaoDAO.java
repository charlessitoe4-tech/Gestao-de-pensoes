package dao;

import java.util.List;
import java.util.Optional;
import model.Permissao;

/** Contrato de acesso a dados para permissões. */
public interface PermissaoDAO {

    void criar(Permissao permissao);

    Optional<Permissao> buscarPorId(Long id);

    Optional<Permissao> buscarPorCodigo(String codigo);

    List<Permissao> listarTodos();

    List<Permissao> listarPorModulo(String modulo);

    void atualizar(Long id, Permissao permissao);

    boolean remover(Long id);
}