package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidacaoDadosMoçambiqueTest {

    @Test
    void validaDocumentosMozambicanosEPermiteSeparadoresDeFormatacao() {
        assertTrue(ValidacaoDadosMoçambique.biValido("1234-5678-9012"));
        assertFalse(ValidacaoDadosMoçambique.biValido("12345678901"));
        assertTrue(ValidacaoDadosMoçambique.nuitValido("123 456 789"));
        assertFalse(ValidacaoDadosMoçambique.nuitValido("12345678A"));
    }

    @Test
    void capitalizaInicialSemAlterarORestanteFormatoDeUnicode() {
        assertEquals("Maria", ValidacaoDadosMoçambique.comInicialMaiuscula("mARIA"));
        assertEquals("Érica", ValidacaoDadosMoçambique.comInicialMaiuscula("érica"));
    }
}
