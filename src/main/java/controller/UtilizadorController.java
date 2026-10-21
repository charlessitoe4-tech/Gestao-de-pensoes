package controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Utilizador;
import service.UtilizadorService;

/**
 * Controlador de aplicação para operações sobre utilizadores.
 *
 * <p>Não depende de Swing; pode ser usado por qualquer camada de apresentação.</p>
 */
public class UtilizadorController {

    private final UtilizadorService service;

    public UtilizadorController() {
        this(new UtilizadorService());
    }

    public UtilizadorController(UtilizadorService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Utilizador guardar(Utilizador utilizador) {
        return service.guardar(utilizador);
    }

    public List<Utilizador> listar() {
        return service.listar();
    }

    public Optional<Utilizador> buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    public Optional<Utilizador> buscarPorUsername(String username) {
        return service.buscarPorUsername(username);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    public void registarLogin(Long id) {
        service.registarLogin(id);
    }

    public Utilizador obter(Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Utilizador não encontrado: " + id));
    }
}