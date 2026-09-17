package bugboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Gestisce il caricamento in memoria delle chiavi crittografiche RSA dal file system.
 */
@Configuration
public class RsaKeyConfig {

    /**
     * Legge e decodifica la chiave pubblica dal file public.pem.
     *
     * @return L'oggetto chiave pubblica istanziato.
     * @throws IOException              Se il file non viene trovato o non è leggibile.
     * @throws NoSuchAlgorithmException Se l'algoritmo RSA non è supportato dall'ambiente.
     * @throws InvalidKeySpecException  Se il formato del file PEM non è valido.
     */
    @Bean
    public RSAPublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        String key = Files.readString(
                Path.of("Keys", "public.pem")
        );

        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    /**
     * Legge e decodifica la chiave privata dal file private.pem.
     *
     * @return L'oggetto chiave privata istanziato.
     * @throws IOException              Se il file non viene trovato o non è leggibile.
     * @throws NoSuchAlgorithmException Se l'algoritmo RSA non è supportato dall'ambiente.
     * @throws InvalidKeySpecException  Se il formato del file PEM non è valido.
     */
    @Bean
    public RSAPrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException  {

        String key = Files.readString(
                Path.of("Keys", "private.pem")
        );

        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
