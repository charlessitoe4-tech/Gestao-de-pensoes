package dao;

import java.util.List;
import java.util.Optional;
import model.Utilizador;

public interface UtilizadorDAO {

    void criar(Utilizador utilizador);

    Optional<Utilizador> buscarPorId(Long id);

    Optional<Utilizador> buscarPorUsername(String username);

    List<Utilizador> listarTodos();

    void atualizar(Long id, Utilizador utilizador);

    boolean remover(Long id);
}