package Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário simples para hash e verificação de senhas.
 * Usa SHA-256 com salt aleatório.
 * Para produção, considerar BCrypt ou Argon2.
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }

    /** Gera hash no formato: base64(salt) + ":" + base64(hash). */
    public static String gerarHash(String senhaPlana) {
        if (senhaPlana == null || senhaPlana.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = hash(senhaPlana, salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    /** Verifica se a senha corresponde ao hash guardado. */
    public static boolean verificar(String senhaPlana, String hashGuardado) {
        if (senhaPlana == null || hashGuardado == null) {
            return false;
        }
        String[] partes = hashGuardado.split(":");
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            byte[] hashCalculado = hash(senhaPlana, salt);
            return MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] hash(String senha, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(senha.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não disponível.", ex);
        }
    }
}