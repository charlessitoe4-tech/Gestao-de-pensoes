package dao;

import java.util.List;
import java.util.Optional;
import model.Pessoa;

/** Contrato de acesso a dados para pessoas. */
public interface PessoaDAO<T extends Pessoa> {

    void criar(T pessoa);

    Optional<T> buscarPorId(Long id);

    Optional<T> buscarPorBI(String numeroBI);

    Optional<T> buscarPorNuit(String nuit);

    List<T> listarTodos();

    void atualizar(Long id, T pessoa);

    boolean remover(Long id);
}