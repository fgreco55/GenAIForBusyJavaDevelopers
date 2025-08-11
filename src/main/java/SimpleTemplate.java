import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SimpleTemplate {
    public static void main(String[] args) {
        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(.3)                    // keep randomness low
                .timeout(Duration.ofSeconds(120))
                .maxTokens(1024)
                .build();

        String myTemplate = "Please explain {{topic}} to a {{student_type}} using a clear, succinct paragraph";
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);

        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", "quantum computing");
        variables.put("student_type", "musician");

        Prompt prompt = promptTemplate.apply(variables);
        String response = cmodel.chat(prompt.text());

        System.out.println(response);
    }
}
