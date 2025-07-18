import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

import java.time.Duration;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChatStreamingService {
    public interface ChatBot {
        @SystemMessage("Respond as a professional business consultant without using markdown or numbered bullets in your response")
        TokenStream sendChat(String userMessage);
    }

    public static void main(String[] args) {
        Scanner userinput;
        String cmdline;

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .timeout(Duration.ofSeconds(120))
                .build();

        ChatBot consultant = AiServices.builder(ChatBot.class)
                .streamingChatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        Set<String> terminate = Set.of("exit", "quit", "bye");
        String pstring = "\nrequest> ";

        while (true) {
            System.out.print(pstring);

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (terminate.contains(cmdline.toLowerCase()))
                break;

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            /*
             The following might be a bit advanced for beginning Java developers.
             I would suggest investigating java.util.concurrent and "asynchronous programming in Java"

             While this handles streaming output, you may experience output that is not as smooth as you expect for a chatbot.
             More code is required to produce smooth output that users expect.
             */

            CompletableFuture<ChatResponse> future = new CompletableFuture<>();
            TokenStream stream = consultant.sendChat(cmdline);
            stream  .onPartialResponse(System.out::print)
                    .onCompleteResponse(future::complete)
                    .onError(future::completeExceptionally)
                    .start();

            future.join();
        }
    }
}
