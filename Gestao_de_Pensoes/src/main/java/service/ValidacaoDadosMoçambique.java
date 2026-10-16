package service;

import java.util.Locale;

/** Normalização e validação de dados usados no contexto moçambicano. */
public final class ValidacaoDadosMoçambique {

    private ValidacaoDadosMoçambique() {
    }

    public static String normalizarDocumento(String documento) {
        return documento == null ? null : documento.trim().replaceAll("[\\s-]", "");
    }

    public static boolean biValido(String numeroBI) {
        String normalizado = normalizarDocumento(numeroBI);
        return normalizado != null && normalizado.matches("\\d{12}");
    }

    public static boolean nuitValido(String nuit) {
        String normalizado = normalizarDocumento(nuit);
        return normalizado != null && normalizado.matches("\\d{9}");
    }

    public static String comInicialMaiuscula(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }
        String normalizado = texto.trim().toLowerCase(Locale.ROOT);
        int primeiroPontoDeCodigo = normalizado.codePointAt(0);
        int fimPrimeiroPontoDeCodigo = Character.charCount(primeiroPontoDeCodigo);
        return new String(Character.toChars(Character.toUpperCase(primeiroPontoDeCodigo)))
                + normalizado.substring(fimPrimeiroPontoDeCodigo);
    }

    public static boolean emailValido(String email) {
        return email != null && email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
