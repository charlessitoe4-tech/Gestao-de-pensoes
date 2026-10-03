package dao;

import java.util.List;
import java.util.Optional;
import model.Pensao;

<<<<<<< HEAD
/** Contrato de acesso a dados para pensões. */
public interface PensaoDAO {

    void criar(Pensao pensao);

    Optional<Pensao> buscarPorId(Long id);

    List<Pensao> listarTodos();

    void atualizar(Long id, Pensao pensao);

    boolean remover(Long id);
}
=======
/** Contrato de persistencia CRUD para pensoes. */
public interface PensaoDAO extends RepositorioCrud<Pensao, Long> {
}
>>>>>>> 0a2bc51333b2b1ccf5584282a4d546c55c5ef3c6
