package controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Relatorio;
import service.RelatorioService;

/** Controlador de aplicação para relatórios. */
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController() {
        this(new RelatorioService());
    }

    public RelatorioController(RelatorioService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Relatorio gerar(Relatorio relatorio) {
        return service.gerar(relatorio);
    }

    public List<Relatorio> listar() {
        return service.listar();
    }

    public List<Relatorio> listarPorTipo(String tipo) {
        return service.listarPorTipo(tipo);
    }

    public Optional<Relatorio> buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public Relatorio obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Relatório não encontrado: " + id));
    }
}