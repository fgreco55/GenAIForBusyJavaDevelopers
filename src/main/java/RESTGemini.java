import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RESTGemini {

    public static void main(String[] args) throws IOException, InterruptedException {
        var apiKey = System.getenv("GOOGLE_API_KEY");
        var body = """
               {
                   "contents": [{
                       "parts": [{    "text" : "Write a clever joke about Java programmers"  }]
                   }]
               }""";


        String googleModelURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash";
        String googleString = googleModelURL + ":" + "generateContent?key=" + apiKey;


        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create(googleString))

                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}

