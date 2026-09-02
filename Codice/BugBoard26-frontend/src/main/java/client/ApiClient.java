package client;

import java.net.http.HttpClient;

public class ApiClient {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpClient getClient() {
        return client;
    }
}
