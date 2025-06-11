import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RESTAnthropic {

    public static void main(String[] args) throws IOException, InterruptedException {
        var apiKey = System.getenv("ANTHROPIC_API_KEY");
        var body = """
               {
                   "model": "claude-3-7-sonnet-20250219",
                   "max_tokens": 1024,
                   "messages": [
                       {
                           "role": "user",
                           "content": "Tell me a good dad joke about Java programmers"
                       }
                   ]
               }
               """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")

                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}

