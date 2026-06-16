package com.sai.ai_chatbot.controller;

import com.sai.ai_chatbot.config.SprintDataLoader;
import com.sai.ai_chatbot.model.*;
import com.sai.ai_chatbot.repository.StoryRepository;
import com.sai.ai_chatbot.service.SprintPlanningService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprint")
@CrossOrigin(origins = "*")
public class SprintPlanningController {

    private final SprintPlanningService sprintPlanningService;
    private final StoryRepository storyRepository;
    private final SprintDataLoader sprintDataLoader;

    public SprintPlanningController(SprintPlanningService sprintPlanningService,
                                    StoryRepository storyRepository,
                                    SprintDataLoader sprintDataLoader) {
        this.sprintPlanningService = sprintPlanningService;
        this.storyRepository = storyRepository;
        this.sprintDataLoader = sprintDataLoader;
    }

    // Endpoint 1: Get backlog stories (HTML expects: GET /api/sprint/backlog)
    @GetMapping("/backlog")
    public List<UserStory> getBacklog() {
        return storyRepository.findByStatus("TODO");
    }

    // Endpoint 2: Plan sprint with agent (HTML expects: POST /api/sprint/plan with {userQuery: "..."})
    @PostMapping("/plan")
    public Map<String, String> planSprint(@RequestBody SprintPlanRequest request) {
        System.out.println("Received request: " + request.userQuery());

        String agentResponse = sprintPlanningService.planSprint(request.userQuery());

        // HTML expects: { "response": "..." }
        return Map.of("response", agentResponse);
    }

    // Endpoint 3: Reload sample data (HTML expects: POST /api/sprint/stories/reload)
    @PostMapping("/stories/reload")
    public Map<String, Object> reloadSampleStories() {
        System.out.println("Reloading sample data via API...");

        // Delete all existing stories
        storyRepository.deleteAll();

        // Reuse the existing loadSampleData method from SprintDataLoader
        sprintDataLoader.loadSampleData();

        long totalCount = storyRepository.count();
        long backlogCount = storyRepository.findByStatus("TODO").size();

        System.out.println("Loaded " + totalCount + " stories (" + backlogCount + " in backlog)");

        // HTML expects: { "message": "...", "newCount": 18, "backlogCount": 10 }
        return Map.of(
                "message", "Sample data reloaded successfully",
                "newCount", totalCount,
                "backlogCount", backlogCount
        );
    }
}