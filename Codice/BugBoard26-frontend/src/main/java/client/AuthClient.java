package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.helper.SendRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import enums.Ruolo;
import model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client HTTP responsabile della comunicazione con le API di backend relative all'autenticazione
 * e alla gestione degli utenti.
 * Si occupa di formattare i payload JSON, inviare le richieste e gestire l'iniezione del token JWT.
 */
public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080/api/user/";

    private final HttpClient client = ApiClient.getClient();

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);


    /**
     * Tenta l'autenticazione dell'utente verso il backend.
     * In caso di successo (HTTP 200), il backend restituisce un token JWT e i dati base dell'utente.
     * Il metodo estrae questi dati e inizializza la sessione globale.
     *
     * @param email    L'email fornita dall'utente.
     * @param password La password fornita dall'utente in chiaro.
     * @return true se l'autenticazione ha successo e la sessione viene popolata.
     * @throws IOException            Errore di comunicazione I/O con il server.
     * @throws InterruptedException   Se il thread di rete viene interrotto.
     * @throws IllegalAccessException Se il server risponde con HTTP 401.
     */
    public boolean login(String email, String password) throws IOException, InterruptedException, IllegalAccessException {
        String json = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "login")).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            JsonNode jsonNode = mapper.readTree(response.body());

            String token = jsonNode.path("token").asText();
            String ruoloString = jsonNode.path("ruoloUtente").asText();
            String idString = jsonNode.path("id").asText();

            if(!token.isEmpty() && !ruoloString.isEmpty() && !idString.isEmpty()) {
                AuthSession.getInstance().setToken(token);

                AuthUser utenteLoggato = new AuthUser();
                utenteLoggato.setRuolo(Ruolo.valueOf(ruoloString));
                utenteLoggato.setEmail(email);
                utenteLoggato.setId(Integer.parseInt(idString));

                AuthSession.getInstance().setUtenteCorrente(utenteLoggato);
                return true;
            }
        } else if (response.statusCode() == 401) {
            throw new IllegalAccessException(response.body());
        }
        return false;
    }


    /**
     * Invia una richiesta al server per modificare la password dell'utente loggato.
     * Necessita del token JWT nell'header per l'autorizzazione.
     *
     * @param email       L'email dell'utente.
     * @param oldPassword La password attuale.
     * @param newPassword La nuova password desiderata.
     * @return true se l'operazione ha successo (HTTP 200), altrimenti false.
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        String json = """
                {
                    "email": "%s",
                    "oldPassword": "%s",
                    "newPassword": "%s"
                }
        """.formatted(email, oldPassword, newPassword);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "cambia-password"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Recupera l'elenco completo degli utenti registrati a sistema.
     * Utilizzato dall'Admin nel pannello di gestione.
     *
     * @return Una lista di oggetti AuthUser. Ritorna una lista vuota in caso di errore.
     */
    public List<AuthUser> getUsers(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "elenco-utenti"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<AuthUser>>() {}, new ArrayList<>(), client);
    }


    /**
     * Richiede le statistiche relative al numero di issue attualmente assegnate a ciascun utente.
     *
     * @return Una mappa in cui la chiave è l'email dell'utente e il valore è il conteggio delle issue in corso.
     */
    public Map<String, Integer> getIssuesPerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "issues"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>(){}, new HashMap<>(), client);
    }


    /**
     * Richiede le statistiche relative al numero di issue risolte da ciascun utente.
     *
     * @return Una mappa in cui la chiave è l'email dell'utente e il valore è il conteggio delle issue risolte.
     */
    public Map<String, Integer> getRisoltePerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "issue-risolte"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>(){}, new HashMap<>(), client);
    }


    /**
     * Richiede il calcolo del tempo medio impiegato da ciascun utente per risolvere le proprie issue.
     *
     * @return Una mappa in cui la chiave è l'email dell'utente e il valore è il tempo medio espresso in ore.
     */
    public Map<String, Double> getTimePerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tempo-per-user"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Double>>() {}, new HashMap<>(), client);
    }

    /**
     * Invia una richiesta PUT per invertire logicamente lo stato di attivazione di un account.
     * Da disabilitato diventa abilitato, e viceversa.
     *
     * @param id L'identificativo univoco dell'utente di cui cambiare lo stato.
     * @return true se l'operazione ha avuto successo, altrimenti false.
     */
    public boolean cambiaStatoUtente(int id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + id + "/cambia-stato"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    /**
     * Invia una richiesta al server per creare un nuovo account utente.
     * Effettuata da un Amministratore.
     *
     * @param email    L'email del nuovo utente (univoca).
     * @param password La password iniziale dell'utente.
     * @param ruolo    Il ruolo assegnato.
     * @return true se la creazione ha successo (HTTP 200 o 201), altrimenti false.
     */
    public boolean registerUser(String email, String password, String ruolo) {
        String json = """
                {
                    "email": "%s",
                    "password": "%s",
                    "ruolo": "%s"
                }
                """.formatted(email, password, ruolo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "crea-utenti"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
