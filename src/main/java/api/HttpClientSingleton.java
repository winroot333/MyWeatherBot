package api;

import java.net.http.HttpClient;

public class HttpClientSingleton {
    private static final HttpClient INSTANCE = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private HttpClientSingleton() {
    }

    public static HttpClient getInstance() {
        return INSTANCE;
    }
}