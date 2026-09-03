package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

            System.out.println("IL MIO TOKEN AL MOMENTO DEL CLICK E': " + AuthSession.getToken());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/home/nuovaIssue"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("CODICE RISPOSTA SERVER: " + response.statusCode());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
