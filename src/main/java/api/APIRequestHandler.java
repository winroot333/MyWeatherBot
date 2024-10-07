package api;

import org.apache.http.client.HttpResponseException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class APIRequestHandler {
    private static final int TIMEOUT_SECONDS = 15;

    public static String request(URI uri) throws HttpResponseException {
        int statusCode = -1;
        try {
            HttpClient httpClient = HttpClientSingleton.getInstance();
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uri)
                            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                            .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            if (statusCode == 200) {
                return response.body();
            } else
                throw new HttpResponseException(statusCode, "API error: " + uri.toString());
        } catch (IOException | InterruptedException e) {
            throw new HttpResponseException(statusCode, "API error: " + uri.toString());
        }
    }
}
