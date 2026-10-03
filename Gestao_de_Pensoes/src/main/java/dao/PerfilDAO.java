package dao;

import java.util.List;
import java.util.Optional;
import model.Perfil;

/** Contrato de acesso a dados para perfis. */
public interface PerfilDAO {

    void criar(Perfil perfil);

    Optional<Perfil> buscarPorId(Long id);

    Optional<Perfil> buscarPorNome(String nome);

    List<Perfil> listarTodos();

    void atualizar(Long id, Perfil perfil);

    boolean remover(Long id);
}