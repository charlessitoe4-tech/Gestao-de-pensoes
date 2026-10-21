package dao;

import java.util.List;
import java.util.Optional;
import model.Configuracao;

/** Contrato de acesso a dados para configurações do sistema. */
public interface ConfiguracaoDAO {

    void criar(Configuracao configuracao);

    Optional<Configuracao> buscarPorId(Long id);

    Optional<Configuracao> buscarPorChave(String chave);

    List<Configuracao> listarTodos();

    List<Configuracao> listarPorGrupo(String grupo);

    void atualizar(Long id, Configuracao configuracao);

    boolean remover(Long id);
}