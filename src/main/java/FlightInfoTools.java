import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightInfoTools {
    private final Logger logger = LoggerFactory.getLogger(FlightInfo.class);

    @Tool("Retrieves the flight status of a given flight")
    String getFlightStatus(String flightId) {
        logger.info("Get flight status of flight {}", flightId);
        return flightId + " status: " + "IN THE AIR.  2 hours from destination";
    }

    @Tool("Returns the type of aircraft")
    String getAircraftType(String flightId) {
        logger.info("Get aircraft type of flight {}", flightId);
        return flightId + " type: " + "Boeing 747";
    }

    @Tool("Returns the cost of a flight")
    String getFlightCost(String flightId) {         // Name of method is helpful context for an LLM to choose a method
        logger.info("Returns the cost of a flight {}", flightId);
        return "1234.00 USD";
    }
}
