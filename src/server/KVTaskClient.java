package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;
    private final HttpClient client;

    public KVTaskClient(URI serverUrl) {
        try {
            url = serverUrl.toString();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI.create(url + "/register"))
                    .build();
            client = HttpClient.newHttpClient();
            apiToken = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("Регистрация на KVServer прошла успешно. API_TOKEN=" + apiToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, String json) {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
