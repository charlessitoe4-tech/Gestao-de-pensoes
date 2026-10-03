package dao;

import java.util.List;
import java.util.Optional;
import model.Pensao;

/** Contrato de acesso a dados para pensões. */
public interface PensaoDAO {

    void criar(Pensao pensao);

    Optional<Pensao> buscarPorId(Long id);

    List<Pensao> listarTodos();

    void atualizar(Long id, Pensao pensao);

    boolean remover(Long id);
}