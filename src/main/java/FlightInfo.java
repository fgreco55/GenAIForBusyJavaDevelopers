import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;

public class FlightInfo {

    interface FlightAssistant {
        String flightInfo(String userMessage);
    }

    public static void main(String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .strictTools(true)
                .logRequests(true)
                //.logResponses(true)
                .build();

        ChatMemory cm = MessageWindowChatMemory.withMaxMessages(10);
        cm.add(SystemMessage.from("You are a helpful, and informative flight agent.  Only use the methods I have described."));  // just for illustrative purposes

        FlightAssistant assistant = AiServices.builder(FlightAssistant.class)
                .chatModel(model)
                .tools(new FlightInfoTools())
                .chatMemory(cm)         // An effective system message that controls/constrains the method choices is critical.
                .build();

        String response;

        response = assistant.flightInfo("I need the status of Flight UA1011");
        System.out.println(response);

        response = assistant.flightInfo("What type of aircraft is it?");
        System.out.println(response);

        response = assistant.flightInfo("What was its cost");
        System.out.println(response);
    }
}
