package controller;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Utilizador;
import service.LoginService;

/** Controlador de autenticação de utilizadores. */
public class LoginController {

    private final LoginService service;

    public LoginController() {
        this(new LoginService());
    }

    public LoginController(LoginService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Optional<Utilizador> autenticar(String username, String senha) {
        return service.autenticar(username, senha);
    }

    public Utilizador autenticarOuFalhar(String username, String senha) {
        return service.autenticar(username, senha)
                .orElseThrow(() -> new NoSuchElementException(
                        "Credenciais inválidas."));
    }
}