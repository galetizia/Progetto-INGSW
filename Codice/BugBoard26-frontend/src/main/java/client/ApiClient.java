package client;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Classe Helper che fornisce un'istanza singola (Singleton) e condivisa di HttpClient.
 * Utilizzare un unico client per tutta l'applicazione migliora drasticamente le performance
 * grazie al connection pooling e al risparmio di memoria.
 */
public class ApiClient {

    private ApiClient() {}

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public static HttpClient getClient() {
        return client;
    }
}
