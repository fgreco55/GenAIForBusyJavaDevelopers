import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SimpleChatCompletion {
    public static void main(String[] args){

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(1024)
                .build();

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("Respond as a very funny comedian.");
        UserMessage usermsg = new UserMessage("How should I learn Java?");

        messages.add(sysmsg);	messages.add(usermsg);

        ChatResponse answer = cmodel.chat(messages);

        System.out.println(answer.aiMessage().text());
    }
}

