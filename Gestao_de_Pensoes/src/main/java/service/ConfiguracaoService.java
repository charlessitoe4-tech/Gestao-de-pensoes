package service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.ConfiguracaoDAO;
import dao.HibernateConfiguracaoDAO;
import model.Configuracao;

/** Regras de negócio para configurações do sistema. */
public class ConfiguracaoService {

    private final ConfiguracaoDAO repositorio;

    public ConfiguracaoService() {
        this(new HibernateConfiguracaoDAO());
    }

    public ConfiguracaoService(ConfiguracaoDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Configuracao guardar(Configuracao configuracao) {
        validar(configuracao);
        if (configuracao.getId() == null) {
            repositorio.criar(configuracao);
        } else {
            repositorio.atualizar(configuracao.getId(), configuracao);
        }
        return configuracao;
    }

    public List<Configuracao> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<Configuracao> listarPorGrupo(String grupo) {
        Objects.requireNonNull(grupo, "O grupo é obrigatório.");
        return repositorio.listarPorGrupo(grupo);
    }

    public Optional<Configuracao> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public Optional<Configuracao> buscarPorChave(String chave) {
        Objects.requireNonNull(chave, "A chave é obrigatória.");
        return repositorio.buscarPorChave(chave);
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    public Configuracao obter(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Configuração não encontrada: " + id));
    }

    private void validar(Configuracao configuracao) {
        Objects.requireNonNull(configuracao, "A configuração é obrigatória.");
        if (configuracao.getChave() == null || configuracao.getChave().isBlank()) {
            throw new IllegalArgumentException("A chave é obrigatória.");
        }
        if (configuracao.getValor() == null) {
            throw new IllegalArgumentException("O valor é obrigatório.");
        }
    }
}