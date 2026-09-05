package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Issue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class IssueClient {


    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient client = ApiClient.getClient();

    public boolean createIssue(String titolo, String descrizione, String priorita, String urlImmagine) {

        try {
            String json = """
                    {
                        "titolo": "%s",
                        "descrizione": "%s",
                        "priorita": "%s",
                        "urlImmagine": "%s"
                    }
                    """.formatted(titolo, descrizione, priorita, urlImmagine);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/home/nuovaIssue"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Issue> elencoIssue() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/home/elenco_issue"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                String json = response.body();

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, new TypeReference<List<Issue>>(){});
            } else
                System.out.println("Errore: " + response.statusCode());
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
