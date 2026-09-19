package client.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Classe Helper che centralizza l'invio delle richieste HTTP verso il backend Spring Boot
 * e la successiva deserializzazione delle risposte JSON in oggetti Java.
 * Configura un ObjectMapper di Jackson ottimizzato per gestire correttamente le date (JavaTimeModule).
 */
public class SendRequest {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Logger logger = LoggerFactory.getLogger(SendRequest.class);

    private SendRequest () {}


    /**
     * Esegue una richiesta HTTP e tenta di deserializzare il corpo
     * della risposta JSON in un tipo di dato Java specifico.
     * Incorpora un meccanismo di fail-safe: in caso di errori di rete, parsing fallito o status code
     * diverso da 200 OK, restituisce un valore di default senza far crashare il chiamante.
     *
     * @param <T>           Il tipo di dato atteso come risultato.
     * @param request       L'oggetto HttpRequest preconfigurato con URI, Header e Metodo.
     * @param typeReference L'oggetto di Jackson necessario per preservare i tipi generici a runtime.
     * @param defaultValue  Il valore di ripiego da restituire in caso di fallimento.
     * @param client        L'istanza di HttpClient responsabile dell'invio fisico della richiesta.
     * @return L'oggetto parsato dal JSON in caso di successo (HTTP 200), altrimenti il defaultValue.
     */
    public static <T> T sendRequestGet(HttpRequest request, TypeReference<T> typeReference, T defaultValue, HttpClient client) {

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                return mapper.readValue(response.body(), typeReference);
            }

        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return defaultValue;
    }


    /**
     * Esegue una richiesta HTTP in cui non è richiesto
     * di leggere o parsare un corpo di risposta, ma interessa esclusivamente l'esito dell'operazione.
     *
     * @param request L'oggetto HttpRequest preconfigurato.
     * @param client  L'istanza di HttpClient responsabile dell'invio fisico della richiesta.
     * @return true se la comunicazione è andata a buon fine e il server ha risposto con HTTP 200,
     *         false in caso di errori di rete, timeout o status code differenti.
     */
    public static boolean sendRequestPut(HttpRequest request, HttpClient client) {

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException e){
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
