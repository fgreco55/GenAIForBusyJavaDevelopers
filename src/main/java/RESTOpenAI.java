import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RESTOpenAI {

    public static void main(String[] args) throws IOException, InterruptedException {
        var apiKey = System.getenv("OPENAI_API_KEY");
        var body = """
        {
            "model": "gpt-4",
            "messages": [
                {
                    "role": "user",
                    "content": "Tell me a funny joke about programming in Java"
                }
            ]
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}

