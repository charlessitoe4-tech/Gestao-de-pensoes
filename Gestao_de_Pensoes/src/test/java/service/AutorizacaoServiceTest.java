package service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Perfil;
import model.Utilizador;
import model.enums.NivelAcesso;
import org.junit.jupiter.api.Test;

class AutorizacaoServiceTest {

    @Test
    void definePermissoesParaOsTresPerfisDaAplicacao() {
        AutorizacaoService administrador = autorizacao(NivelAcesso.ADMINISTRADOR);
        AutorizacaoService gestor = autorizacao(NivelAcesso.GESTOR);
        AutorizacaoService operador = autorizacao(NivelAcesso.OPERADOR);

        assertTrue(administrador.podeGerirUtilizadores());
        assertTrue(administrador.podeEliminarDados());
        assertTrue(gestor.podeEliminarDados());
        assertFalse(gestor.podeGerirUtilizadores());
        assertTrue(operador.podeEditarDados());
        assertFalse(operador.podeEliminarDados());
        assertFalse(operador.podeGerirUtilizadores());
    }

    private AutorizacaoService autorizacao(NivelAcesso nivel) {
        Perfil perfil = new Perfil();
        perfil.setNivelAcesso(nivel);
        Utilizador utilizador = new Utilizador();
        utilizador.setPerfil(perfil);
        return new AutorizacaoService(utilizador);
    }
}
