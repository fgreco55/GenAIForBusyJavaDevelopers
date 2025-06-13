import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

public class ChatService {

    public interface ChatBot {
        String sendChat(String userMessage);
    }

    public static void main(String[] args) {
        Scanner userinput;
        String cmdline;

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        ChatBot chatBot = AiServices.builder(ChatBot.class)
                .chatModel(model)
                .chatMemory(memory)
                .build();

        while (true) {
            System.out.print("prompt> ");

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            System.out.println(chatBot.sendChat(cmdline));
        }
    }
}
