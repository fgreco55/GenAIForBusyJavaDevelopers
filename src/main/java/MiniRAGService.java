import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class MiniRAGService {
    interface Assistant {
        @SystemMessage("You are a polite assistant")
        String chat(String text);
    }

    public static void main(String[] args) throws IOException {
        String cmdline;

        EmbeddingModel emodel = createEmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> myDB = new InMemoryEmbeddingStore<>();   // in-memory embedding store

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/java-faq.txt"));
        List<Document> documents = lines.stream()
                .map(String::trim)                          // remove leading/trailing whitespace
                .filter(line -> !line.isEmpty())      // skip empty strings
                .map(Document::from)                        // convert to Document
                .collect(Collectors.toList());

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.<TextSegment>builder()
                .documentSplitter(new DocumentBySentenceSplitter(512, 25))
                .embeddingStore(myDB)
                .embeddingModel(emodel)
                .build();

        ingestor.ingest(documents);

        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(emodel)
                .embeddingStore(myDB)
                .maxResults(5)
                .minScore(.7)
                .build();

        ChatModel model = createChatModel();

        Assistant cb = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        while (true) {
            if ((cmdline = getInput("Cmd> ")).isEmpty())
                continue;

            var response = cb.chat(cmdline);        // Send everything to the LLM
            System.out.println(response);
        }
    }

    /**
     * getInput() - convenience method to get the user input
     * @param userPrompt    - Display command-line prompt
     * @return user's inputted string
     */
    public static String getInput(String userPrompt) {
        System.out.print(userPrompt);
        return new Scanner(System.in).nextLine();
    }

    /**
     * createChatModel() - convenience method to instantiate a ChatModel
     * @return specific chat model (for our example, hard-coded to OpenAI)
     */
    public static ChatModel createChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();
    }

    /**
     * createEmbeddingModel() - convenience method to instantiate an EmbeddingModel
     * @return embedding model - in our case, hard-coded to OpenAI
     */
    public static EmbeddingModel createEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()          // Select the embedding algorithm/service
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();
    }

    /*private class Chatbot {
        private ChatModel chatModel;
        private EmbeddingModel embeddingModel;
        private EmbeddingStore embeddingStore;
        private int maxResults;
        private int maxMemory;
        private float minScore;

        Chatbot(ChatModel chatModel, EmbeddingModel embModel, EmbeddingStore embStore, int maxres, int maxmem, float minscore) {
            this.chatModel = chatModel;
            this.embeddingModel = embeddingModel;
            this.embeddingStore = embeddingStore;
            this.maxResults = maxres;
            this.maxMemory = maxmem;
            this.minScore = minscore;
        }
        Chatbot(ChatModel chatModel) {
            this.chatModel = chatModel;
        }
    }*/
}
