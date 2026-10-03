package service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import Util.PasswordUtil;
import dao.HibernateUtilizadorDAO;
import dao.UtilizadorDAO;
import model.Utilizador;
import Util.PasswordUtil;

/** Autenticação de utilizadores. */
public class LoginService {

    private final UtilizadorDAO repositorio;

    public LoginService() {
        this(new HibernateUtilizadorDAO());
    }

    public LoginService(UtilizadorDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    /**
     * Autentica o utilizador.
     *
     * @return utilizador autenticado, ou vazio se credenciais inválidas
     */
    public Optional<Utilizador> autenticar(String username, String senhaPlana) {
        if (username == null || username.isBlank() || senhaPlana == null || senhaPlana.isBlank()) {
            return Optional.empty();
        }
        Optional<Utilizador> encontrado = repositorio.buscarPorUsername(username.trim());
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }
        Utilizador u = encontrado.get();
        if (!u.isAtivo()) {
            return Optional.empty();
        }
        PasswordUtil PasswordUtil = null;
        if (!PasswordUtil.verificar(senhaPlana, u.getSenhaHash())) {
            return Optional.empty();
        }
        // registar último login
        u.setUltimoLogin(LocalDateTime.now());
        repositorio.atualizar(u.getId(), u);
        return Optional.of(u);
    }
}