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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class MiniRAGService {
    interface Assistant {
        @SystemMessage("You are a polite and very detailed Java consultant")
        String chat(String text);
    }

    public static void main(String[] args) throws IOException {
        String cmdline;

        EmbeddingModel emodel = createEmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> myDB = new InMemoryEmbeddingStore<>();   // in-memory embedding store

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/java-faq.txt"));

        List<Document> documents = convertLinesToDocuments(lines);  // See two implementations below

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.<TextSegment>builder()
                .documentSplitter(new DocumentBySentenceSplitter(512, 25))
                .embeddingStore(myDB)
                .embeddingModel(emodel)
                .build();

        ingestor.ingest(documents);

        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(emodel)
                .embeddingStore(myDB)
                .maxResults(5)          // At most return this number of results
                .minScore(.7)           // Return results that are relevant (0->1 least->most)
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

    /**
     * convertLinesToDocuments(List<String> mylines)
     * Use Java streams to convert a List of Strings to a List of Documents.
     * Ignore the blank lines and trim all the others.
     * See alternative non-streams implementation below
     */
    public static List<Document> xconvertLinesToDocuments(List<String> mylines) {
        return mylines.stream()
                .map(String::trim)                          // remove leading/trailing whitespace
                .filter(line -> !line.isEmpty())      // skip empty strings
                .map(Document::from)                        // convert to Document
                .collect(Collectors.toList());
    }
    /**
     * convertLinesToDocuments(List<String> mylines)
     * Use conventional Java to convert a List of Strings to a List of Documents.
     * Ignore the blank lines and trim all the others.
     * This version might be easier for beginning Java developers.
     */
    public static List<Document> convertLinesToDocuments(List<String> mylines) {
        List<Document> documents = new ArrayList<>();

        for (String line : mylines) {
            String trimmed = line.trim(); // remove leading/trailing whitespace
            if (!trimmed.isEmpty()) {     // skip empty strings
                Document doc = Document.from(trimmed); // convert to Document
                documents.add(doc);
            }
        }
        return documents;
    }
}
