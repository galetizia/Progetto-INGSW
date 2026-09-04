package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient client = ApiClient.getClient();

    public boolean login(String email, String password) throws IOException, InterruptedException {

        String json = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login")).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            String body = response.body();

            String token = extractToken(body);

            AuthSession.setToken(token);
            return true;
        }
        return false;
    }

    public void logout() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/auth/logout"))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            AuthSession.clearToken();
        }
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {

        String json = """
                {
                    "email": "%s",
                    "oldPassword": "%s",
                    "newPassword": "%s"
                }
        """.formatted(email, newPassword, oldPassword);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/auth/change_password"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static String extractToken(String json) {
        return json.replace("{\"token\":\"", "").replace("\"", "").replace("}", "").trim();
    }
}
