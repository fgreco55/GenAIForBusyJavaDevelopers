import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SimpleSystem {
    public static void main(String[] argv) {

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                //.temperature(0.3)                    // keep randomness low
                .timeout(Duration.ofSeconds(120))
                //.maxTokens(256)                     // keep costs down during development
                .build();
        System.out.println("Hello World SimpleSystem ---------------------");
        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("""
                    You are a polite Java expert explaining concepts to a politician.
                """);
        messages.add(sysmsg);

        UserMessage usrmsg = UserMessage.from("Why should I learn Java lambdas?");
        messages.add(usrmsg);

        ChatResponse answer = cmodel.chat(messages);

        System.out.println(answer.aiMessage().text());
    }
}
