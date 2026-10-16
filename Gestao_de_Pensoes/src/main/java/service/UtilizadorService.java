package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.HibernateUtilizadorDAO;
import dao.UtilizadorDAO;
import Util.PasswordUtil;
import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;

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
        return guardar(utilizador, false);
    }

    private Utilizador guardar(Utilizador utilizador, boolean inicializacaoSistema) {
        validar(utilizador);
        String username = utilizador.getUsername().trim().toLowerCase(java.util.Locale.ROOT);
        Optional<Utilizador> existente = repositorio.buscarPorUsername(username);
        if (existente.isPresent()
                && !Objects.equals(existente.get().getId(), utilizador.getId())) {
            throw new IllegalArgumentException("O username já está em utilização.");
        }
        utilizador.setUsername(username);
        if (utilizador.getId() == null) {
            if (utilizador.isContaProtegida() && !inicializacaoSistema) {
                throw new IllegalArgumentException(
                        "Só a configuração inicial pode criar contas protegidas.");
            }
            utilizador.setDataCriacao(LocalDateTime.now());
            repositorio.criar(utilizador);
        } else {
            Utilizador atual = repositorio.buscarPorId(utilizador.getId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Utilizador não encontrado: " + utilizador.getId()));
            if (atual.isContaProtegida() && !inicializacaoSistema) {
                if (!Objects.equals(atual.getUsername(), username)
                        || !Objects.equals(nivel(atual), nivel(utilizador))) {
                    throw new IllegalArgumentException(
                            "O username e o perfil de uma conta principal não podem ser alterados.");
                }
                if (!utilizador.isAtivo()) {
                    throw new IllegalStateException("Uma conta principal não pode ser desativada.");
                }
                utilizador.setContaProtegida(true);
            }
            repositorio.atualizar(utilizador.getId(), utilizador);
        }
        return utilizador;
    }

    public Utilizador registar(String nome, String username, String email,
            String senha, Perfil perfil) {
        if (senha == null || senha.length() < 10) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 10 caracteres.");
        }
        Utilizador utilizador = new Utilizador();
        utilizador.setNome(nome == null ? null : nome.trim());
        utilizador.setUsername(username == null ? null : username.trim());
        utilizador.setEmail(email == null || email.isBlank() ? null : email.trim());
        utilizador.setSenhaHash(PasswordUtil.gerarHash(senha));
        utilizador.setPerfil(perfil);
        utilizador.setAtivo(true);
        if (usernameReservado(utilizador.getUsername())) {
            throw new IllegalArgumentException("Este username está reservado para uma conta principal.");
        }
        return guardar(utilizador);
    }

    public Utilizador guardarContaPrincipal(Utilizador utilizador, NivelAcesso perfilEsperado) {
        Objects.requireNonNull(utilizador, "O utilizador é obrigatório.");
        Objects.requireNonNull(perfilEsperado, "O perfil principal é obrigatório.");
        String username = utilizador.getUsername() == null
                ? "" : utilizador.getUsername().trim().toLowerCase(java.util.Locale.ROOT);
        if (!perfilPrincipal(username, perfilEsperado)) {
            throw new IllegalArgumentException("O username e o perfil principal não correspondem.");
        }
        utilizador.setUsername(username);
        utilizador.setAtivo(true);
        utilizador.setContaProtegida(true);
        return guardar(utilizador, true);
    }

    public boolean eContaProtegida(Long id) {
        return buscarPorId(id).map(Utilizador::isContaProtegida).orElse(false);
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
        Optional<Utilizador> utilizador = repositorio.buscarPorId(id);
        if (utilizador.isEmpty()) {
            return false;
        }
        if (utilizador.get().isContaProtegida()) {
            throw new IllegalStateException("As contas principais do sistema não podem ser eliminadas.");
        }
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
        if (utilizador.getEmail() != null && !utilizador.getEmail().isBlank()
                && !utilizador.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("O email do utilizador não é válido.");
        }
    }

    private NivelAcesso nivel(Utilizador utilizador) {
        return utilizador.getPerfil() == null ? null : utilizador.getPerfil().getNivelAcesso();
    }

    private boolean usernameReservado(String username) {
        return username != null && switch (username.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "admin", "gestor", "operador" -> true;
            default -> false;
        };
    }

    private boolean perfilPrincipal(String username, NivelAcesso perfil) {
        return switch (username) {
            case "admin" -> perfil == NivelAcesso.ADMINISTRADOR;
            case "gestor" -> perfil == NivelAcesso.GESTOR;
            case "operador" -> perfil == NivelAcesso.OPERADOR;
            default -> false;
        };
    }
}