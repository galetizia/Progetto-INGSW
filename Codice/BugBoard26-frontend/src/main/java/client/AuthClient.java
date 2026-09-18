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

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080/api/user/";

    private final HttpClient client = ApiClient.getClient();

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);


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


    public List<AuthUser> getUsers(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "elenco-utenti"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<List<AuthUser>>() {}, new ArrayList<>(), client);
    }


    public Map<String, Integer> getIssuesPerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "issues"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>(){}, new HashMap<>(), client);
    }


    public Map<String, Integer> getRisoltePerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "issue-risolte"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Integer>>(){}, new HashMap<>(), client);
    }


    public Map<String, Double> getTimePerUser(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tempo-per-user"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .GET().build();
        return SendRequest.sendRequestGet(request, new TypeReference<Map<String, Double>>() {}, new HashMap<>(), client);
    }

    public boolean cambiaStatoUtente(int id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + id + "/cambia-stato"))
                .header("Authorization", "Bearer " + AuthSession.getInstance().getToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return SendRequest.sendRequestPut(request, client);
    }


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
