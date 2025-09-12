import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.mistralai.MistralAiChatModel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HelloWorldSystemMistral {
    public static void main(String[] argv) {
        ChatModel cmodel = MistralAiChatModel.builder()
                .apiKey(System.getenv("MISTRAL_API_KEY"))
                .modelName(String.valueOf(MistralAiChatModelName.OPEN_MISTRAL_7B))
                .timeout(Duration.ofSeconds(120))
                .maxTokens(256)
                .build();

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("""
                    You are a polite Java expert explaining concepts to a C++ software developer using examples.
                """);
        messages.add(sysmsg);

        UserMessage usrmsg = UserMessage.from("What are Java lambdas?");
        messages.add(usrmsg);

        var answer = cmodel.chat(messages);

        System.out.println(answer.aiMessage().text());
    }
}
