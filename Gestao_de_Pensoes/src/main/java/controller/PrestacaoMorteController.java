package controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.PrestacaoMorte;
import model.enums.TipoPrestacao;
import service.PrestacaoMorteService;

/** Controlador de aplicação para prestações por morte. */
public class PrestacaoMorteController {

    private final PrestacaoMorteService service;

    public PrestacaoMorteController() {
        this(new PrestacaoMorteService());
    }

    public PrestacaoMorteController(PrestacaoMorteService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public PrestacaoMorte guardar(PrestacaoMorte prestacao) {
        return service.guardar(prestacao);
    }

    public PrestacaoMorte aprovar(Long id) {
        return service.aprovar(id);
    }

    public List<PrestacaoMorte> listar() {
        return service.listar();
    }

    public List<PrestacaoMorte> listarSubsidiosMorte() {
        return service.listarSubsidiosMorte();
    }

    public List<PrestacaoMorte> listarSubsidiosFuneral() {
        return service.listarSubsidiosFuneral();
    }

    public List<PrestacaoMorte> listarPorTipo(TipoPrestacao tipo) {
        return service.listar().stream()
                .filter(p -> p.getTipo() == tipo)
                .toList();
    }

    public Optional<PrestacaoMorte> buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public PrestacaoMorte obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Prestação não encontrada: " + id));
    }
}