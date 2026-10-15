package Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Gera e verifica hashes de senha com suporte aos hashes SHA-256 legados. */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALGORITMO = "pbkdf2-sha256";
    private static final int ITERACOES = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_BITS = 256;

    private PasswordUtil() {
    }

    /** Gera um hash PBKDF2 com salt aleatório e parâmetros armazenados no próprio hash. */
    public static String gerarHash(String senhaPlana) {
        if (senhaPlana == null || senhaPlana.isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senhaPlana.toCharArray(), salt, ITERACOES, HASH_BITS);
        return ALGORITMO + "$" + ITERACOES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /** Verifica hashes PBKDF2 e hashes SHA-256 antigos, sem interromper utilizadores existentes. */
    public static boolean verificar(String senhaPlana, String hashGuardado) {
        if (senhaPlana == null || hashGuardado == null) {
            return false;
        }
        if (hashGuardado.startsWith(ALGORITMO + "$")) {
            return verificarPbkdf2(senhaPlana, hashGuardado);
        }
        return verificarSha256Legado(senhaPlana, hashGuardado);
    }

    public static boolean precisaAtualizacao(String hashGuardado) {
        return hashGuardado == null || !hashGuardado.startsWith(ALGORITMO + "$");
    }

    private static boolean verificarPbkdf2(String senha, String hashGuardado) {
        String[] partes = hashGuardado.split("\\$", -1);
        if (partes.length != 4 || !ALGORITMO.equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 1 || iteracoes > 2_000_000) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);
            if (salt.length < 8 || hashEsperado.length == 0) {
                return false;
            }
            byte[] hashCalculado = derivar(senha.toCharArray(), salt, iteracoes,
                    hashEsperado.length * Byte.SIZE);
            return MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean verificarSha256Legado(String senha, String hashGuardado) {
        String[] partes = hashGuardado.split(":", -1);
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashCalculado = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (IllegalArgumentException ex) {
            return false;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não está disponível.", ex);
        }
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes, int bits) {
        PBEKeySpec especificacao = new PBEKeySpec(senha, salt, iteracoes, bits);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacao).getEncoded();
        } catch (java.security.GeneralSecurityException ex) {
            throw new IllegalStateException("PBKDF2 não está disponível.", ex);
        } finally {
            especificacao.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}
