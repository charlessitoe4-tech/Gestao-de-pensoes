package controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.ProvaVida;
import service.ProvaVidaService;

/** Controlador de aplicação para provas de vida. */
public class ProvaVidaController {

    private final ProvaVidaService service;

    public ProvaVidaController() {
        this(new ProvaVidaService());
    }

    public ProvaVidaController(ProvaVidaService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public ProvaVida registar(ProvaVida provaVida) {
        return service.registar(provaVida);
    }

    public ProvaVida confirmar(Long id) {
        return service.confirmar(id);
    }

    public ProvaVida marcarFalhada(Long id, String motivo) {
        return service.marcarFalhada(id, motivo);
    }

    public List<ProvaVida> listar() {
        return service.listar();
    }

    public List<ProvaVida> listarPorPensionista(Long pensionistaId) {
        return service.listarPorPensionista(pensionistaId);
    }

    public Optional<ProvaVida> buscarMaisRecente(Long pensionistaId) {
        return service.buscarMaisRecente(pensionistaId);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public ProvaVida obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Prova de vida não encontrada: " + id));
    }
}