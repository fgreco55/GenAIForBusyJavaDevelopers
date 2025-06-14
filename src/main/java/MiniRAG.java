import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class MiniRAG {
    public static void main(String[] args) {
        String cmdline;

        InMemoryEmbeddingStore<TextSegment> myDB = new InMemoryEmbeddingStore<>();   // in-memory embedding store

        EmbeddingModel emodel = createEmbeddingModel();
        loadEmbeddingsFromFile(myDB, emodel, "src/main/resources/rag_repository.txt");

        ChatModel model = createChatModel();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);    // Lab: convert this pgm to AiService

        while (true) {
            if ((cmdline = getInput("Cmd> ")).isEmpty())
                continue;

            UserMessage usrmsg = UserMessage.from(cmdline);             // create the initial prompt

            List<String> match = getMatches(myDB, emodel, cmdline);     // Get list of matches from the DB

            for (String m: match) {                                     // Add matches to the memory
                memory.add(UserMessage.from(m));
            }

            memory.add(usrmsg);                                         // finally, add the user's prompt

            var answer = model.chat(memory.messages());                 // Send everything to the LLM
            var response = answer.aiMessage().text();

            System.out.println(response);

            memory.add(UserMessage.from(response));                     // Add response from llm
            memory.add(UserMessage.from(cmdline));                      // Add prompt from the user
        }
    }

    /**
     * getInput() - convenience method to get the user input
     * @param userPrompt
     * @return
     */
    public static String getInput(String userPrompt) {
        System.out.print(userPrompt);
        return new Scanner(System.in).nextLine();
    }

    /**
     * createChatModel() - convenience method to instantiate a ChatModel
     * @return
     */
    public static ChatModel createChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();
    }

    /**
     * createEmbeddingModel() - convenience method to instantiate an EmbeddingModel
     * @return
     */
    public static EmbeddingModel createEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()          // Select the embedding algorithm/service
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();
    }

    /**
     * loadEmbeddingsFromFile() - read a text file, create an embedding vector from each line, and store into an EmbeddingStore
     * @param estore
     * @param embModel
     * @param myfile
     */
    public static void loadEmbeddingsFromFile(EmbeddingStore<TextSegment> estore, EmbeddingModel embModel, String myfile) {

        try (Stream<String> lines = Files.lines(Path.of(myfile))) {
            lines.forEach(line -> {
                if (!line.isEmpty()) {
                    TextSegment ts = TextSegment.from(line);
                    Embedding emb = embModel.embed(ts).content();
                    estore.add(emb, ts);
                    //System.out.println("Storing: [" + ts.text() + "]");
                }
            });
            System.out.println("Embedding and Loading completed...");
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    /**
     * getMatches() - Search an InMemorryEmbeddingStore for content related to a given string
     * @param myDB
     * @param embModel
     * @param input
     * @return
     */
    public static List<String> getMatches(InMemoryEmbeddingStore<TextSegment> myDB, EmbeddingModel embModel, String input) {
        List<String> myList = new ArrayList<>();

        Embedding queryEmbedding = embModel.embed(input).content();     // create embedding vector from cmdline

        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()    // Search the local embedding store for related strings
                .queryEmbedding(queryEmbedding)
                .maxResults(10)     // at most, find this number of matches [best in a properties file]
                .minScore(0.7)      // 0-1, so ignore anything below the midpoint [best in a properties file]
                .build();

        List<EmbeddingMatch<TextSegment>> matches = myDB.search(embeddingSearchRequest).matches();

        if (!matches.isEmpty()) {
            for (EmbeddingMatch<TextSegment> match : matches) {
                String s = match.embedded().text();
                myList.add(s);
                // System.out.println("MATCH: " + s);
            }
        }
        return myList;
    }
}
