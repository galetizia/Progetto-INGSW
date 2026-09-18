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

public class IssueClient {

    private static final String BASE_URL = "http://localhost:8080/api/issues/";

    private final HttpClient client = ApiClient.getClient();

    private static final Logger logger = LoggerFactory.getLogger(IssueClient.class);


    private void aggiungiCampoTesto(List<byte[]> byteArrays, String boundary, String nome, String valore) {
        String campo = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + nome + "\"\r\n\r\n" +
                valore + "\r\n";
        byteArrays.add(campo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }


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
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    public boolean prendiInCarico(int issueId){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/prendi-in-carico"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    public boolean risolviIssue(int issueId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/risolvi"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    public boolean rilasciaIssue(int issueId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + issueId + "/rilascia"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


    public List<Issue> getIssueAttive() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "attive"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    public List<Issue> getIssueArchiviate() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "storico"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    public List<Issue> elencoIssue() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "elenco-issue"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<Issue>>() {}, new ArrayList<>(), client);
    }


    public Map<String, Integer> countIssueStates() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "count-issue-states"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>() {}, new HashMap<>(), client);
    }


    public Map<String, Integer> countIssueTypes() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "count-issue-types"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET()
                .build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>() {}, new HashMap<>(), client);
    }


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
            logger.error(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }


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
            logger.error(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }
}
