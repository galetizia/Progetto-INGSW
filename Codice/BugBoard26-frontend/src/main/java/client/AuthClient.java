package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public List<AuthUser> getUsers(){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "elenco_utenti"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                String json = response.body();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, new TypeReference<List<AuthUser>>(){});
            } else System.out.println(response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();


    }


    // Metodo creazione utente
    public boolean registerUser(String email, String password, String ruolo) {
        String json = """
                {
                    "email": "%s",
                    "password": "%s",
                    "ruolo": "%s"
                }
                """.formatted(email, password, ruolo);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "crea_utenti"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code Creazione Utente: " + response.statusCode());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.out.println("Errore dal server: " + response.body());
            }

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo Attiva/Disattiva utente
    public boolean cambiaStatoUtente(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + id + "/cambia_stato"))
                    .header("Authorization", "Bearer " + AuthSession.getToken())
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code Cambio Stato: " + response.statusCode());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
