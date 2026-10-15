package service;

import java.util.Objects;
import model.Utilizador;
import model.enums.NivelAcesso;

/** Regras de autorização da aplicação desktop. */
public final class AutorizacaoService {

    private final NivelAcesso nivel;

    public AutorizacaoService(Utilizador utilizador) {
        Objects.requireNonNull(utilizador, "O utilizador autenticado é obrigatório.");
        if (utilizador.getPerfil() == null || utilizador.getPerfil().getNivelAcesso() == null) {
            throw new IllegalArgumentException("O utilizador não tem um perfil de acesso válido.");
        }
        this.nivel = utilizador.getPerfil().getNivelAcesso();
    }

    public boolean podeEditarDados() {
        return nivel == NivelAcesso.ADMINISTRADOR
                || nivel == NivelAcesso.GESTOR
                || nivel == NivelAcesso.OPERADOR;
    }

    public boolean podeEliminarDados() {
        return nivel == NivelAcesso.ADMINISTRADOR || nivel == NivelAcesso.GESTOR;
    }

    public boolean podeGerirUtilizadores() {
        return nivel == NivelAcesso.ADMINISTRADOR;
    }

    public NivelAcesso getNivel() {
        return nivel;
    }
}
