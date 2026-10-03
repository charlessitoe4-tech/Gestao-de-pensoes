package controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Configuracao;
import service.ConfiguracaoService;

/** Controlador de aplicação para configurações do sistema. */
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    public ConfiguracaoController() {
        this(new ConfiguracaoService());
    }

    public ConfiguracaoController(ConfiguracaoService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Configuracao guardar(Configuracao configuracao) {
        return service.guardar(configuracao);
    }

    public List<Configuracao> listar() {
        return service.listar();
    }

    public List<Configuracao> listarPorGrupo(String grupo) {
        return service.listarPorGrupo(grupo);
    }

    public Optional<Configuracao> buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public Optional<Configuracao> buscarPorChave(String chave) {
        return service.buscarPorChave(chave);
    }

    public String obterValor(String chave, String valorPadrao) {
        return service.buscarPorChave(chave)
                .map(Configuracao::getValor)
                .orElse(valorPadrao);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public Configuracao obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Configuração não encontrada: " + id));
    }
}