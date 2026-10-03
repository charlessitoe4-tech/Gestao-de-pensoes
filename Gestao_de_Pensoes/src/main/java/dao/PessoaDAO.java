package dao;

import java.util.List;
import java.util.Optional;
import model.Pessoa;

/** Contrato de acesso a dados para pessoas. */
public interface PessoaDAO {

    void criar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(Long id);

    Optional<Pessoa> buscarPorBI(String numeroBI);

    Optional<Pessoa> buscarPorNuit(String nuit);

    List<Pessoa> listarTodos();

    void atualizar(Long id, Pessoa pessoa);

    boolean remover(Long id);
}