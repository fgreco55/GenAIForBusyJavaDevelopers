import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.*;

public class TravelPlannerApp {

    public interface TravelAgent {
        String planTrip(String userRequest);
    }

    public static void main(String[] args) {

        var model = OpenAiChatModel.builder()
                .modelName("gpt-4o-mini")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.1)      // keep low to try to avoid hallucinations if possible
                .build();

        var tools = new TravelTools();

        TravelAgent agent = AiServices.builder(TravelAgent.class)
                .chatModel(model)
                .tools(tools)
                .systemMessage("""
                        You are a practical travel planner agent.
                        Your job: propose a realistic itinerary within the user's budget and constraints.
                        
                        Hard rules:
                        - You MUST call getHotelOptions AND searchAttractions before proposing a final itinerary.
                        - Ask at most 2 clarifying questions total; otherwise make reasonable assumptions and proceed.
                        - Do not invent venue/hotel facts beyond tool output.
                        - Keep the plan walkable/transit-friendly if requested.
                        - If either tool returns "No ... data available", STOP.
                        - Do not provide a general itinerary. Ask the user for a supported city or explain the limitation.
                        
                        
                        Output structure (exact headings):
                        1) Assumptions
                        2) Shortlist (Hotels)
                        3) Shortlist (Attractions / Food / Jazz)
                        4) 3-Day Itinerary (Morning / Afternoon / Evening)
                        5) Budget Estimate (table)
                        6) Next Questions (max 2)
                        
                        Budget policy:
                        - Prefer staying under budget; if not possible, propose a cheaper alternative.
                        - Include a 10% buffer in estimates.
                        """)
                .build();

        String userRequest = """
                Plan a 3-day weekend in new york for 2 people.
                We like jazz and great food. Total budget under $2000 (USD).
                We prefer walkable neighborhoods and public transit.
                """;
        System.out.println("RESPONSE ===================================");
        System.out.println(agent.planTrip(userRequest));
    }
}
