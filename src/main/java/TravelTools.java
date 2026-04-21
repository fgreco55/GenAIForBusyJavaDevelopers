import dev.langchain4j.agent.tool.Tool;

import java.util.*;
import java.util.stream.Collectors;

public class TravelTools {

    private interface CityItem {        // small interface for city-handling in both HotelOption/AttractionOption
        String city();
    }
    record HotelOption(String city, String name, String neighborhood, int pricePerNightUsd, double rating, String note)
            implements CityItem {}
    record AttractionOption(String city, String name, String category, String bestTime, int estimatedCostPerPersonUsd, String note)
            implements CityItem {}

    // ---- City-aware stub data ----

    private final List<HotelOption> hotels = List.of(
            // Montreal
            new HotelOption("Montreal", "Mile End Micro-Hotel", "Mile End", 145, 4.2, "Trendy neighborhood, small rooms"),
            new HotelOption("Montreal", "Hotel Chez Metro", "Plateau-Mont-Royal", 155, 4.3, "Great transit access, simple rooms"),
            new HotelOption("Montreal", "Downtown Value Suites", "Downtown", 165, 4.0, "Convenient, no-frills"),
            new HotelOption("Montreal", "Old Port Budget Inn", "Old Montreal", 175, 4.1, "Walkable, touristy area"),
            new HotelOption("Montreal", "Luxury Center Hotel", "Downtown", 260, 4.7, "Nice but pricey"),

            // New York
            new HotelOption("New York", "LIC Budget Hotel", "Long Island City", 170, 4.1, "Great subway access into Manhattan"),
            new HotelOption("New York", "Brooklyn Walkable Inn", "Williamsburg", 190, 4.2, "Food + transit-rich neighborhood"),
            new HotelOption("New York", "Queens Value Stay", "Astoria", 175, 4.0, "Solid value; quick ride to Midtown"),
            new HotelOption("New York", "Downtown Value Stay", "Lower Manhattan", 220, 4.0, "Central but can get pricey"),
            new HotelOption("New York", "Midtown Business Hotel", "Midtown", 260, 4.3, "Convenient, but usually expensive")
    );

    private final List<AttractionOption> attractions = List.of(
            // Montreal
            new AttractionOption("Montreal", "Jazz club: Upstairs Jazz Bar & Grill", "Jazz", "Evening", 25, "Classic jazz venue vibe"),
            new AttractionOption("Montreal", "Jazz club: Dièse Onze", "Jazz", "Evening", 30, "Intimate live sets"),
            new AttractionOption("Montreal", "Food: Schwartz's Deli", "Food", "Any", 25, "Iconic smoked meat"),
            new AttractionOption("Montreal", "Food: Jean-Talon Market", "Food", "Morning", 20, "Market stroll + snacks"),
            new AttractionOption("Montreal", "Sight: Old Montreal walk", "Sightseeing", "Afternoon", 0, "Historic streets + views"),
            new AttractionOption("Montreal", "Sight: Mount Royal overlook", "Sightseeing", "Morning", 0, "Great city view"),
            new AttractionOption("Montreal", "Museum: Montreal Museum of Fine Arts", "Museum", "Afternoon", 24, "Strong collection"),
            new AttractionOption("Montreal", "Neighborhood: Mile End café crawl", "Food", "Afternoon", 20, "Coffee + pastries"),

            // New York
            new AttractionOption("New York", "Jazz: Village Vanguard", "Jazz", "Evening", 45, "Legendary basement club"),
            new AttractionOption("New York", "Jazz: Smalls", "Jazz", "Evening", 40, "Late-night sets, tight room"),
            new AttractionOption("New York", "Jazz: Dizzy's Club at Jazz at Lincoln Center", "Jazz", "Evening", 55, "Big views + big band energy"),
            new AttractionOption("New York", "Food: Katz's Delicatessen", "Food", "Any", 30, "Iconic deli experience"),
            new AttractionOption("New York", "Food: Chelsea Market", "Food", "Afternoon", 25, "Food hall with many options"),
            new AttractionOption("New York", "Sight: High Line walk", "Sightseeing", "Afternoon", 0, "Walkable elevated park"),
            new AttractionOption("New York", "Sight: Central Park loop", "Sightseeing", "Morning", 0, "Classic walkable NYC"),
            new AttractionOption("New York", "Neighborhood: West Village stroll", "Sightseeing", "Afternoon", 0, "Walkable streets + cafes")
    );

    // **************************************************************************
    @Tool("Get hotel options given a city, nightly budget, preferred neighborhoods (comma-separated), and number of nights. Returns top matches.")
    public String getHotelOptions(String city, int maxNightlyUsd, String neighborhoodsCsv, int nights) {
        System.err.println("Get hotel options...");
        String normalizedCity = normalizeCity(city);
        Set<String> neighborhoodSet = parseCsvLower(neighborhoodsCsv);

        List<HotelOption> cityHotels = hotels.stream()
                .filter(h -> h.city().equalsIgnoreCase(normalizedCity))
                .toList();

        if (cityHotels.isEmpty()) {
            return "No hotel data available for city=" + city
                    + ". Supported cities: " + supportedCities(hotels);
        }

        List<HotelOption> results = cityHotels.stream()
                .filter(h -> h.pricePerNightUsd() <= maxNightlyUsd)
                .filter(h -> neighborhoodSet.isEmpty() || neighborhoodSet.contains(h.neighborhood().toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparingDouble((HotelOption h) -> h.rating()).reversed()
                        .thenComparingInt(HotelOption::pricePerNightUsd))
                .limit(4)
                .toList();

        if (results.isEmpty()) {
            // Relax neighborhood constraint, still same city + budget
            results = cityHotels.stream()
                    .filter(h -> h.pricePerNightUsd() <= maxNightlyUsd)
                    .sorted(Comparator.comparingInt(HotelOption::pricePerNightUsd))
                    .limit(4)
                    .toList();
        }

        return results.stream()
                .map(h -> String.format("- %s | neighborhood=%s | nightlyUSD=%d | rating=%.1f | note=%s",
                        h.name(), h.neighborhood(), h.pricePerNightUsd(), h.rating(), h.note()))
                .collect(Collectors.joining("\n"));
    }

    // **************************************************************************
    @Tool("Search attractions, restaurants, and jazz venues for a city and interests (comma-separated). Returns a compact shortlist.")
    public String searchAttractions(String city, String interestsCsv, String timeOfDayPreference) {
        System.err.println("Search attractions...");

        String normalizedCity = normalizeCity(city);
        Set<String> interests = parseCsvLower(interestsCsv);

        List<AttractionOption> cityAttractions = attractions.stream()
                .filter(a -> a.city().equalsIgnoreCase(normalizedCity))
                .toList();

        if (cityAttractions.isEmpty()) {
            return "No attractions data available for city=" + city
                    + ". Supported cities: " + supportedCities(attractions);
        }

        List<AttractionOption> results = cityAttractions.stream()
                .filter(a -> interests.isEmpty() || interests.stream()
                        .anyMatch(i -> a.category().toLowerCase(Locale.ROOT).contains(i)))
                .filter(a -> timeOfDayPreference == null || timeOfDayPreference.isBlank()
                        || a.bestTime().equalsIgnoreCase("Any")
                        || a.bestTime().equalsIgnoreCase(timeOfDayPreference))
                .limit(8)
                .toList();

        if (results.isEmpty()) {
            // fallback within the same city
            results = cityAttractions.stream().limit(6).toList();
        }

        return results.stream()
                .map(a -> String.format("- %s | category=%s | bestTime=%s | estCostPerPersonUSD=%d | note=%s",
                        a.name(), a.category(), a.bestTime(), a.estimatedCostPerPersonUsd(), a.note()))
                .collect(Collectors.joining("\n"));
    }

    // **************************************************************************
    @Tool("Estimate trip costs for a 3-day weekend. Returns a markdown table breakdown including a buffer.")
    public String estimateCosts(int travelers,
                                int nights,
                                int hotelNightlyUsd,
                                int mealsPerDayPerPersonUsd,
                                int transitPerDayPerPersonUsd,
                                int attractionsPerPersonUsd,
                                int bufferPercent) {
        System.err.println("Estimate trip costs...");
        int days = 3; // fixed for this lab

        int lodging = hotelNightlyUsd * nights;
        int meals = travelers * mealsPerDayPerPersonUsd * days;
        int transit = travelers * transitPerDayPerPersonUsd * days;
        int attractions = travelers * attractionsPerPersonUsd;

        int subtotal = lodging + meals + transit + attractions;
        int buffer = (int) Math.round(subtotal * (bufferPercent / 100.0));
        int total = subtotal + buffer;

        return """
                    | Item | Amount (USD) |
                    |---|---:|
                    | Lodging | %d |
                    | Meals | %d |
                    | Transit | %d |
                    | Attractions / Entertainment | %d |
                    | Subtotal | %d |
                    | Buffer (%d%%) | %d |
                    | **Total** | **%d** |
                    """.formatted(lodging, meals, transit, attractions, subtotal, bufferPercent, buffer, total);
    }



    // ----------------- Helpers -----------------

    private static Set<String> parseCsvLower(String csv) {
        if (csv == null) return Set.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    /**
     * Normalize common city synonyms to the canonical names used by stub data.
     */
    private static String normalizeCity(String city) {
        if (city == null) return "";
        String c = city.trim().toLowerCase(Locale.ROOT);
        return switch (c) {
            case "ny", "nyc", "new york city", "new york" -> "New York";
            case "mtl", "montréal", "montreal" -> "Montreal";
            default -> city.trim();
        };
    }

    /**
     * Compute supported cities dynamically from stub data.
     */
    private static String supportedCities(List<? extends CityItem> items) {
        return items.stream()
                .map(CityItem::city)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
    }
}
