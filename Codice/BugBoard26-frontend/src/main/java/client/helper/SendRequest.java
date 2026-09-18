package client.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SendRequest {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Logger logger = LoggerFactory.getLogger(SendRequest.class);

    private SendRequest () {}


    public static <T> T sendRequestGet(HttpRequest request, TypeReference<T> typeReference, T defaultValue, HttpClient client) {

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                return mapper.readValue(response.body(), typeReference);
            }

        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return defaultValue;
    }


    public static boolean sendRequestPut(HttpRequest request, HttpClient client) {

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException e){
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
