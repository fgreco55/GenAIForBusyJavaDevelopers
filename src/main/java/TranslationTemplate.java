import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_SONNET_20241022;

public class TranslationTemplate {
    public static void main(String[] args) {

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(.3)                    // keep randomness low
                .timeout(Duration.ofSeconds(120))
                .maxTokens(5*1024)
                .build();

        String myTemplate = "Translate {{mytext}} from English to {{language}} and then back to English.  Show the delta between the original English and your translated English";
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);

        while (true) {
            String text = getUserInput("Text> ");
            String language = getUserInput("Language> ");

            Map<String, Object> variables = new HashMap<>();
            variables.put("mytext", text);
            variables.put("language", language);
            Prompt prompt = promptTemplate.apply(variables);

            System.out.println("ORIGINAL: " + text);
            System.out.println("=====================================");
            System.out.println(prompt.text());
            System.out.println("=====================================");

            var resp = model.chat(prompt.text());
            System.out.println(resp);
        }
    }

    /**
     * getUserInput(String pstring)
     * @param pstring - string that reminds the user what to enter
     * @return What the user typed in
     * minimal error checking...
     */
    static public String getUserInput(String pstring) {
        String cmdline;

        System.out.print(pstring);    // prompt the user
        cmdline = new Scanner(System.in).nextLine();
        return cmdline.isBlank() ? "" : cmdline;
    }
}
