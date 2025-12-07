package com.ticketml.services.impl;

import com.ticketml.common.dto.gemini.*;
import com.ticketml.common.entity.Event;
import com.ticketml.common.entity.Ticket;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.TicketStatus;
import com.ticketml.repository.EventRepository;
import com.ticketml.repository.TicketRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.GeminiService;
import com.ticketml.specification.EventSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class GeminiServiceImpl implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiServiceImpl.class);

    private final WebClient webClient;
    private final String geminiUrl;
    private final String apiKey;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    private final Map<String, List<ContentDTO>> chatMemory = new ConcurrentHashMap<>();

    public GeminiServiceImpl(WebClient webClient,
                             @Value("${gemini.url}") String geminiUrl,
                             @Value("${gemini.api-key}") String apiKey,
                             EventRepository eventRepository,
                             UserRepository userRepository,
                             TicketRepository ticketRepository) {
        this.webClient = webClient;
        this.geminiUrl = geminiUrl;
        this.apiKey = apiKey;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public String generateContent(String userMessage, String googleId) {
        List<ContentDTO> history = chatMemory.computeIfAbsent(googleId, k -> new ArrayList<>());

        if (history.isEmpty()) {
            initializeContext(history, googleId);
        }

        ContentDTO userContent = new ContentDTO("user", Collections.singletonList(new PartDTO(userMessage)));
        history.add(userContent);

        GeminiResponseDTO response = callGeminiApi(history, getTools());

        if (response == null || response.getCandidates().isEmpty()) {
            return "Sorry, AI service is busy.";
        }

        PartDTO responsePart = response.getCandidates().get(0).getContent().getParts().get(0);

        if (responsePart.getFunctionCall() != null) {
            FunctionCallDTO funcCall = responsePart.getFunctionCall();
            String functionName = funcCall.getName();
            logger.info("Gemini wants to call function: {}", functionName);

            history.add(response.getCandidates().get(0).getContent());

            Map<String, Object> executionResult = executeFunction(functionName, funcCall.getArgs());

            FunctionResponseDTO funcResponse = new FunctionResponseDTO(functionName, executionResult);
            PartDTO resultPart = new PartDTO();
            resultPart.setFunctionResponse(funcResponse);
            ContentDTO functionContent = new ContentDTO("function", Collections.singletonList(resultPart));
            history.add(functionContent);

            GeminiResponseDTO finalResponse = callGeminiApi(history, null);

            history.add(finalResponse.getCandidates().get(0).getContent());

            return finalResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
        }

        history.add(response.getCandidates().get(0).getContent());
        return responsePart.getText();
    }

    private void initializeContext(List<ContentDTO> history, String googleId) {
        User user = userRepository.findByGoogleId(googleId).orElse(null);
        if (user != null) {
            String userContext = "System Context: User's name is " + user.getFullName() + ". ";

            List<Ticket> pastTickets = ticketRepository.findTicketsByUserAndStatus(user.getId(), TicketStatus.USED);
            if (!pastTickets.isEmpty()) {
                StringBuilder historyBuilder = new StringBuilder("User history: User has attended these events: ");
                pastTickets.stream().limit(5).forEach(t ->
                        historyBuilder.append(t.getTicketType().getEvent().getTitle()).append(", ")
                );
                userContext += historyBuilder.toString();
            } else {
                userContext += "User is new and hasn't bought any tickets yet.";
            }

            // Thêm system instruction và fake response
            history.add(new ContentDTO("user", Collections.singletonList(new PartDTO(userContext))));
            history.add(new ContentDTO("model", Collections.singletonList(new PartDTO("Understood. I will remember this context."))));
        }
    }

    private Map<String, Object> executeFunction(String name, Map<String, Object> args) {
        if ("find_events".equals(name)) {
            String keyword = (String) args.get("keyword");
            String location = (String) args.get("location");
            String startDateStr = (String) args.get("start_date");
            String endDateStr = (String) args.get("end_date");

            Specification<Event> spec = Specification.allOf();

            if (StringUtils.hasText(keyword)) {
                spec = spec.and(EventSpecification.hasTitleLike(keyword));
            }
            if (StringUtils.hasText(location)) {
                spec = spec.and(EventSpecification.hasLocationLike(location));
            }
            try {
                if (StringUtils.hasText(startDateStr)) {
                    spec = spec.and(EventSpecification.hasStartDateAfterOrEqual(LocalDate.parse(startDateStr)));
                }
                if (StringUtils.hasText(endDateStr)) {
                    spec = spec.and(EventSpecification.hasEndDateBeforeOrEqual(LocalDate.parse(endDateStr)));
                }
            } catch (Exception e) {
                logger.warn("Date parsing error from AI: {}", e.getMessage());
            }

            List<Event> events = eventRepository.findAll(spec);
            if (events.isEmpty()) {
                return Map.of("result", "No events found matching criteria.");
            }

            List<Map<String, Object>> resultList = events.stream().limit(5)
                    .map(this::mapEventToInfo)
                    .toList();

            return Map.of("events", resultList);
        }
        return Map.of("error", "Function not found");
    }

    private Map<String, Object> mapEventToInfo(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", event.getId());
        map.put("title", event.getTitle());
        map.put("location", event.getLocation());
        map.put("start_date", event.getStartDate() != null ? event.getStartDate().toString() : "N/A");

        List<String> ticketInfo = event.getTicketTypes().stream()
                .map(tt -> String.format("%s: %s VND (Còn %d/%d)",
                        tt.getType(),
                        String.format("%,.0f", tt.getPrice()),
                        tt.getRemainingQuantity(),
                        tt.getTotalQuantity()))
                .collect(Collectors.toList());

        map.put("ticket_types", ticketInfo);
        return map;
    }

    private GeminiResponseDTO callGeminiApi(List<ContentDTO> contents, List<ToolDTO> tools) {
        GeminiRequestDTO request = new GeminiRequestDTO(contents, tools);
        return webClient.post()
                .uri(geminiUrl)
                .header("x-goog-api-key", apiKey)
                .body(Mono.just(request), GeminiRequestDTO.class)
                .retrieve()
                .bodyToMono(GeminiResponseDTO.class)
                .block();
    }

    private List<ToolDTO> getTools() {
        FunctionDeclarationDTO findEventsFunc = FunctionDeclarationDTO.builder()
                .name("find_events")
                .description("Search for events based on keywords, location, or date. Use this to check ticket availability and prices.")
                .parameters(Map.of(
                        "type", "OBJECT",
                        "properties", Map.of(
                                "keyword", Map.of("type", "STRING", "description", "Event title or description keywords"),
                                "location", Map.of("type", "STRING", "description", "City or specific venue"),
                                "start_date", Map.of("type", "STRING", "description", "Filter events starting after this date. Format: YYYY-MM-DD"),
                                "end_date", Map.of("type", "STRING", "description", "Filter events ending before this date. Format: YYYY-MM-DD")
                        )
                ))
                .build();

        return Collections.singletonList(new ToolDTO(Collections.singletonList(findEventsFunc)));
    }

}
