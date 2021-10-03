package part1;

import com.google.gson.Gson;
import model.LiftUsage;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import util.RandomNumberGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SkiHttpClient {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public boolean postJson(String url, String json) throws IOException, InterruptedException {

        int retry = 5;
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        while (retry > 0) {
            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
            // print response headers
//        HttpHeaders headers = response.headers();
//        headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

            // print status code
//        System.out.println(response.statusCode());
            int statusCode = response.statusCode();
            if (statusCode != 200 && statusCode != 201) {
                retry = retry - 1;
            } else {
                // print response body
//                System.out.println("\n\nbody is: \n\n" + response.body());
                return true;
            }
        }
        return false;
    }

}