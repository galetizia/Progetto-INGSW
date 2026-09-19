package client;

import client.helper.SendRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import model.Issue;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client HTTP responsabile della comunicazione con le API REST del backend dedicate alla gestione delle Issue.
 * Gestisce l'intero ciclo di vita delle segnalazioni: creazione, cambi di stato, recupero liste e raccolta statistiche.
 */
public class IssueClient {

    private static final String BASE_URL = "http://localhost:8080/api/issues/";

    private final HttpClient client = ApiClient.getClient();

    private static final Logger logger = LoggerFactory.getLogger(IssueClient.class);

    /**
     * Metodo di supporto interno per la costruzione manuale del payload "multipart/form-data".
     * Converte una singola coppia chiave-valore testuale nel formato standard HTTP multipart
     * e l'aggiunge alla lista di byte che comporrà il corpo della richiesta finale.
     *
     * @param byteArrays La lista di array di byte che rappresenta il corpo in costruzione.
     * @param boundary   Il separatore univoco generato per dividere le varie parti del form.
     * @param nome       Il nome del campo.
     * @param valore     Il valore testuale del campo.
     */
    private void aggiungiCampoTesto(List<byte[]> byteArrays, String boundary, String nome, String valore) {
        String campo = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + nome + "\"\r\n\r\n" +
                valore + "\r\n";
        byteArrays.add(campo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }


    /**
     * Invia una richiesta per la creazione di una nuova Issue.
     * Utilizza il formato "multipart/form-data" assemblando manualmente i byte per permettere
     * l'invio combinato di campi testuali e di un eventuale file binario.
     *
     * @param titolo      Il titolo riassuntivo della segnalazione.
     * @param descrizione Il testo dettagliato della issue.
     * @param priorita    Il livello di priorità (opzionale).
     * @param tipologia   La categoria della segnalazione.
     * @param file        Eventuale file immagine da allegare.
     * @return true se la creazione va a buon fine (HTTP 200 o 201), altrimenti false.
     */
    public boolean createIssue(String titolo, String descrizione, String priorita, String tipologia, File file) {

        try {
            String boundary = "Boundary-" + System.currentTimeMillis();
            List<byte[]> data = new ArrayList<>();

            aggiungiCampoTesto(data, boundary, "titolo", titolo);
            aggiungiCampoTesto(data, boundary, "descrizione", descrizione);
            aggiungiCampoTesto(data, boundary, "tipologia", tipologia);

            if(priorita != null && !priorita.isBlank()) {
                aggiungiCampoTesto(data, boundary, "priorita", priorita);
            }


            if(file != null && file.exists()) {
                String fileHeader = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                        "Content-Type: application/octet-stream\r\n\r\n";
                data.add(fileHeader.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                data.add(java.nio.file.Files.readAllBytes(file.toPath()));
                data.add("\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }


            data.add(("--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "nuova-issue"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofByteArrays(data))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (InterruptedException e){
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Notifica al server che l'utente attualmente loggato ha deciso di prendere in carico la segnalazione.
     * Lo stato passerà in ASSEGNATO.
     *
     * @param issueId L'identificativo della issue.
     * @return true in caso di successo.
     */
    public boolean prendiInCarico(int issueId){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/prendi-in-carico"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    /**
     * Invia la richiesta per marcare una issue come "RISOLTO".
     *
     * @param issueId L'identificativo della issue.
     * @return true in caso di successo.
     */
    public boolean risolviIssue(int issueId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/risolvi"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    /**
     * Annulla la presa in carico di una issue, rimuovendo l'assegnatario e riportandola nello stato "TO_DO".
     *
     * @param issueId L'identificativo della issue.
     * @return true in caso di successo.
     */
    public boolean rilasciaIssue(int issueId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/rilascia"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    /**
     * Recupera dal server la lista di tutte le issue attualmente attive.
     *
     * @return La lista degli oggetti Issue.
     */
    public List<Issue> getIssueAttive() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "attive"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    /**
     * Recupera dal server lo storico delle issue archiviate.
     *
     * @return La lista degli oggetti Issue archiviati.
     */
    public List<Issue> getIssueArchiviate() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "storico"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    /**
     * Recupera l'elenco generale di tutte le issue, senza filtri lato backend.
     *
     * @return La lista completa delle issue accessibili.
     */
    public List<Issue> elencoIssue() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "elenco-issue"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    /**
     * Conta il numero di issue raggruppate per stato.
     * Utilizzato per popolare i grafici nella dashboard dell'Admin.
     *
     * @return Una mappa con la chiave indicante lo stato e il valore indicante il conteggio.
     */
    public Map<String, Integer> countIssueStates() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "count-issue-states"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>() {}, new HashMap<>(), client);
    }


    /**
     * Conta il numero di issue raggruppate per tipologia.
     * Utilizzato per popolare i grafici nella dashboard Admin.
     *
     * @return Una mappa con la chiave indicante la tipologia e il valore indicante il conteggio.
     */
    public Map<String, Integer> countIssueTypes() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "count-issue-types"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>() {}, new HashMap<>(), client);
    }


    /**
     * Invia una richiesta critica per eliminare definitivamente una issue dal database.
     *
     * @param issueId L'identificativo della issue da rimuovere.
     * @return true se l'eliminazione ha successo (HTTP 200).
     */
    public boolean eliminaIssue(int issueId) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + issueId + "/elimina"))
                    .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Invia una richiesta per chiudere una issue, spostandola nello stato ARCHIVIATO.
     *
     * @param issueId L'identificativo della issue da archiviare.
     * @return true in caso di successo.
     */
    public boolean archiviaIssue(int issueId) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + issueId + "/archivia"))
                    .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
