import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.Scanner;

public class ChatWithNoContext {
    public static void main(String[] args) {
        Scanner userinput;      // user inputted line as a Scanner
        String cmdline;

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        while (true) {
            System.out.print("prompt> ");

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            UserMessage usrmsg = UserMessage.from(cmdline);
            var answer = cmodel.chat(usrmsg);

            System.out.println(answer.aiMessage().text());
        }
    }
}
