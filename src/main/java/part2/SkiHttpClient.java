package part2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SkiHttpClient {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private Record result;

    public Record getResult() {
        return result;
    }

    public boolean postJson(String url, String json) throws IOException, InterruptedException {

        int retry = 5;
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        Timestamp start = new Timestamp(System.currentTimeMillis());
        int statusCode = 200;
        while (retry > 0) {
//            start = new Timestamp(System.currentTimeMillis());
            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            statusCode = response.statusCode();
            if (statusCode != 200 && statusCode != 201) {
                retry = retry - 1;
            } else {
                Timestamp end = new Timestamp(System.currentTimeMillis());
                long diff = end.getTime() - start.getTime();
                long ms = TimeUnit.MILLISECONDS.toMillis(diff);
                result = new Record(start.getTime(), "POST", diff, String.valueOf(statusCode));
                return true;
            }
        }
        Timestamp end = new Timestamp(System.currentTimeMillis());
        long diff = end.getTime() - start.getTime();
        result = new Record(start.getTime(), "POST", diff, String.valueOf(statusCode));
        return false;
    }

}