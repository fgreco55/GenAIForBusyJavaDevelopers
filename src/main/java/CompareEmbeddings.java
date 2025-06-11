import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

public class CompareEmbeddings {
    public static void main(String[] args) throws IOException {

        EmbeddingModel model = OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();

        List<Float> one = getEmbeddingVec(model, "I like the Java programming language.");
        List<Float> two = getEmbeddingVec(model, "Pineapple does not belong on pizza.");

        double similarity = cosineSimilarity(FloatList2doubleArray(one), FloatList2doubleArray(two));
        System.out.println("Cosine Similarity: " + similarity);
    }

    /**
     * getEmbeddingVec() - retrieve embedding vector for a string using LC4J
     * @param model - which embedding model
     * @param input - target string
     * @return List of embeddings for target string
     */
    public static List<Float> getEmbeddingVec(EmbeddingModel model, String input) {
        Response<Embedding> response = model.embed(input);
        return response.content().vectorAsList();
    }

    /**
     * cosineSimilarity - calculate similarity of 2 embeddings using cosine algorithm
     * @param vec1 - first embedding vector
     * @param vec2 - second embedding vector
     * @return similarity (0.0-1.0) - low to high, dissimilar to similar
     */
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = dotProduct(vec1, vec2);
        double magnitudeVec1 = magnitude(vec1);
        double magnitudeVec2 = magnitude(vec2);

        if (magnitudeVec1 == 0 || magnitudeVec2 == 0) {
            return 0; // To avoid division by zero
        } else {
            return dotProduct / (magnitudeVec1 * magnitudeVec2);
        }
    }

    /**
     * FloatLis2doubleArray() - utility routine to convert a list of Floats to a list of doubles
     * @param floatList - List of Floats
     * @return array of doubles equivalent
     */
    public static double[] FloatList2doubleArray(List<Float> floatList) {
        double[] result = new double[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            result[i] = floatList.get(i);
        }
        return result;
    }

    /**
     * dotProduct() - calculate the matrix dot product of 2 vectors
     * @param vec1 - first vector
     * @param vec2 - second vector
     * @return dot product
     */
    public static double dotProduct(double[] vec1, double[] vec2) {
        double dotProduct = 0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
        }
        return dotProduct;
    }

    // Function to calculate the magnitude of a vector
    public static double magnitude(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }
}

