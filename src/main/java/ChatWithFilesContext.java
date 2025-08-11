import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ChatWithFilesContext {
    public static void main(String[] args) {
        Scanner userinput;      // user inputted line as a Scanner
        String cmdline;
        String folder = "src/main/resources/";

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        List<String> myfiles = Arrays.asList(folder + "java-24.txt", folder + "java-25.txt");
        String filesString = filesToStrings(myfiles);

        while (true) {
            List<ChatMessage> usermessages = new ArrayList<>();

            System.out.print("prompt> ");

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            usermessages.add(UserMessage.from(filesString));
            usermessages.add(UserMessage.from(cmdline));

            var answer = cmodel.chat(usermessages);

            System.out.println(answer.aiMessage().text());
        }
    }

    /**
     * addFiles() - add an optional list of files to the context (very primitive RAG system)
     *
     * @param filenames
     */
    public static String filesToStrings(List<String> filenames) {
        String bigString = "";

        for (String f : filenames) {
            String finfo = null;
            try {
                finfo = new String(Files.readAllBytes(Paths.get(f)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bigString += finfo;
        }
        return bigString;
    }
}
