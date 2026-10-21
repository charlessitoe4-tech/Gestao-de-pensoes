package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.HibernateUtilizadorDAO;
import dao.UtilizadorDAO;
import model.Utilizador;

/** Regras de negócio para gestão de utilizadores. */
public class UtilizadorService {

    private final UtilizadorDAO repositorio;

    public UtilizadorService() {
        this(new HibernateUtilizadorDAO());
    }

    public UtilizadorService(UtilizadorDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Utilizador guardar(Utilizador utilizador) {
        validar(utilizador);
        if (utilizador.getId() == null) {
            utilizador.setDataCriacao(LocalDateTime.now());
            repositorio.criar(utilizador);
        } else {
            repositorio.atualizar(utilizador.getId(), utilizador);
        }
        return utilizador;
    }

    public List<Utilizador> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public Optional<Utilizador> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public Optional<Utilizador> buscarPorUsername(String username) {
        return repositorio.buscarPorUsername(username);
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    public void registarLogin(Long id) {
        Utilizador u = repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Utilizador não encontrado: " + id));
        u.setUltimoLogin(LocalDateTime.now());
        repositorio.atualizar(id, u);
    }

    private void validar(Utilizador utilizador) {
        Objects.requireNonNull(utilizador, "O utilizador é obrigatório.");
        if (utilizador.getNome() == null || utilizador.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (utilizador.getUsername() == null || utilizador.getUsername().isBlank()) {
            throw new IllegalArgumentException("O username é obrigatório.");
        }
        if (utilizador.getSenhaHash() == null || utilizador.getSenhaHash().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (utilizador.getPerfil() == null) {
            throw new IllegalArgumentException("O perfil é obrigatório.");
        }
    }
}