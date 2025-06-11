public class SimpleChatCompletion {
    public static void main(String[] args){

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(30))
                .maxTokens(1024)
                .build();

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage sysmsg = new SystemMessage("Respond as a very funny comedian.");
        UserMessage usermsg = new UserMessage("How should I learn Java?");

        messages.add(sysmsg);	messages.add(usermsg);

        Response<AiMessage> answer = cmodel.chat(messages);

        System.out.println(answer.content().text());
    }
}

