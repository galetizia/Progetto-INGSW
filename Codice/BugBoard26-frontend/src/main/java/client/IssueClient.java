package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Issue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class IssueClient {


    private static final String BASE_URL = "http://localhost:8080/api/issues/";

    private final HttpClient client = ApiClient.getClient();

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
                    .uri(URI.create(BASE_URL + "nuovaIssue"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofByteArrays(data))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code dal Server: " + response.statusCode());
            System.out.println("Messaggio dal Server: " + response.body());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Issue> elencoIssue() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "elenco_issue"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                String json = response.body();

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                return mapper.readValue(json, new TypeReference<List<Issue>>(){});
            } else
                System.out.println("Errore: " + response.statusCode());
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean prendiInCarico(int issueId){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "prendiInCarico?id=" + issueId))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Issue> getIssueAttive() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issues/attive"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                return mapper.readValue(response.body(), new TypeReference<List<Issue>>(){});
            } else {
                System.out.println("Errore getIssueAttive: " + response.statusCode());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Issue> getIssueArchiviate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issues/storico"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                return mapper.readValue(response.body(), new TypeReference<List<Issue>>(){});
            } else {
                System.out.println("Errore getIssueArchiviate: " + response.statusCode());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean archiviaIssue(int issueId) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/issues/" + issueId + "/archivia"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
