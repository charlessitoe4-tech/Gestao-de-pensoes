package Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Gera e verifica hashes de senha.
 *
 * <p>Os hashes novos usam PBKDF2-HMAC-SHA256 com salt aleatório. A verificação
 * também reconhece o formato SHA-256 legado para permitir a migração de contas
 * existentes.</p>
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALGORITMO = "pbkdf2-sha256";
    private static final int ITERACOES = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_BITS = 256;

    private PasswordUtil() {
    }

    /**
     * Gera um hash PBKDF2-HMAC-SHA256 com salt aleatório.
     *
     * <p>O hash devolvido contém o algoritmo, o número de iterações, o salt e o
     * resultado, codificados em Base64 onde aplicável.</p>
     *
     * @param senhaPlana senha que será transformada em hash
     * @return hash codificado para armazenamento
     * @throws IllegalArgumentException se a senha for nula ou vazia
     */
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

    /**
     * Verifica uma senha contra um hash PBKDF2 ou um hash SHA-256 legado.
     *
     * @param senhaPlana senha apresentada para autenticação
     * @param hashGuardado hash persistido da conta
     * @return {@code true} se a senha corresponder ao hash; {@code false} caso
     *         contrário ou se algum argumento for nulo
     */
    public static boolean verificar(String senhaPlana, String hashGuardado) {
        if (senhaPlana == null || hashGuardado == null) {
            return false;
        }
        if (hashGuardado.startsWith(ALGORITMO + "$")) {
            return verificarPbkdf2(senhaPlana, hashGuardado);
        }
        return verificarSha256Legado(senhaPlana, hashGuardado);
    }

    /**
     * Indica se o hash precisa ser substituído pelo formato PBKDF2 atual.
     *
     * <p>Hashes nulos e hashes que não usam o prefixo PBKDF2 são considerados
     * legados. Depois de uma autenticação válida, o chamador pode gerar e
     * persistir um novo hash com {@link #gerarHash(String)}.</p>
     *
     * @param hashGuardado hash persistido da conta
     * @return {@code true} se for necessário atualizar o hash
     */
    public static boolean precisaAtualizacao(String hashGuardado) {
        return hashGuardado == null || !hashGuardado.startsWith(ALGORITMO + "$");
    }

    /**
     * Verifica um hash PBKDF2 validando os parâmetros e comparando os resultados
     * em tempo constante.
     *
     * @param senha senha apresentada
     * @param hashGuardado hash PBKDF2 codificado
     * @return {@code true} se a senha corresponder a um hash válido
     */
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

    /**
     * Verifica o formato legado {@code salt:hash}, ambos codificados em Base64,
     * usando SHA-256.
     *
     * @param senha senha apresentada
     * @param hashGuardado hash legado
     * @return {@code true} se a senha corresponder ao hash
     * @throws IllegalStateException se SHA-256 não estiver disponível na JVM
     */
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

    /**
     * Deriva uma chave PBKDF2-HMAC-SHA256 e limpa a cópia da senha recebida.
     *
     * @param senha caracteres da senha; o array é apagado antes do retorno
     * @param salt salt usado na derivação
     * @param iteracoes número de iterações PBKDF2
     * @param bits tamanho do resultado em bits
     * @return chave derivada
     * @throws IllegalStateException se PBKDF2-HMAC-SHA256 não estiver disponível
     */
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
