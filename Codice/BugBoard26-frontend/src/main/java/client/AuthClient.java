package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import enums.Ruolo;
import model.AuthUser;

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080/api/user/";

    private final HttpClient client = ApiClient.getClient();

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
            String body = response.body();

            String token = estraiValore(body, "token");
            String ruoloString = estraiValore(body, "ruoloUtente");

            if (token != null && ruoloString != null) {
                AuthSession.setToken(token);
                AuthUser utenteLoggato = new AuthUser();
                utenteLoggato.setRuolo(Ruolo.valueOf(ruoloString));
                AuthSession.setUtenteCorrente(utenteLoggato);
                return true;
            }
        } else if (response.statusCode() == 401) {
            throw new IllegalAccessException(response.body());
        }
        return false;
    }

    public void logout() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "logout"))
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
                    .uri(URI.create(BASE_URL + "change_password"))
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

    private String estraiValore(String json, String chiave) {
        String patternString = "\"" + chiave + "\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
