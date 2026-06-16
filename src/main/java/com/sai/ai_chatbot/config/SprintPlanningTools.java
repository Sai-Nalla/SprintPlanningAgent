package com.sai.ai_chatbot.config;

import com.sai.ai_chatbot.model.*;
import com.sai.ai_chatbot.repository.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SprintPlanningTools {

    private static final Logger log = LoggerFactory.getLogger(SprintPlanningTools.class);

    private final StoryRepository storyRepository;

    public SprintPlanningTools(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Tool(description = "Fetch all user stories from the backlog that are in TODO status and not yet assigned to any sprint")
    public List<UserStory> fetchBacklogStories() {
        log.info("Fetching backlog stories...");
        List<UserStory> stories = storyRepository.findByStatusAndSprintNumberIsNull("TODO");
        log.info("Found {} stories in backlog", stories.size());
        return stories;
    }

    @Tool(description = "Find similar user stories from past sprints based on description keywords. " +
            "This helps estimate story points by comparing with similar completed stories.")
    public List<UserStory> findSimilarStories(
            @ToolParam(description = "The description of the current story to find similar matches for")
            String storyDescription) {

        log.info("Finding similar stories for: {}", storyDescription);

        // Extract keywords from description (simple approach - split by spaces)
        String[] keywords = storyDescription.toLowerCase().split("\\s+");

        // Find stories with matching keywords that are already completed
        List<UserStory> allCompleted = storyRepository.findByStatus("DONE");

        List<UserStory> similar = allCompleted.stream()
                .filter(story -> {
                    String desc = story.description().toLowerCase();
                    // Check if at least 2 keywords match
                    long matchCount = 0;
                    for (String keyword : keywords) {
                        if (desc.contains(keyword) && keyword.length() > 3) { // Ignore small words
                            matchCount++;
                        }
                    }
                    return matchCount >= 2;
                })
                .limit(5) // Return top 5 similar stories
                .collect(Collectors.toList());

        log.info("Found {} similar stories", similar.size());
        return similar;
    }

    @Tool(description = "Calculate team velocity by analyzing the total story points completed in the last N sprints. " +
            "Velocity helps determine how many points the team can commit to in the upcoming sprint.")
    public VelocityResult getTeamVelocity(
            @ToolParam(description = "Number of past sprints to analyze (typically 3-5)")
            int lastNSprints) {

        log.info("Calculating team velocity for last {} sprints", lastNSprints);

        // Get all completed stories
        List<UserStory> completedStories = storyRepository.findByStatus("DONE");

        // Group by sprint number and calculate points per sprint
        var sprintMap = completedStories.stream()
                .filter(s -> s.sprintNumber() != null && s.sprintNumber() > 0)
                .collect(Collectors.groupingBy(UserStory::sprintNumber));

        // Get last N sprints
        List<Integer> recentSprints = sprintMap.keySet().stream()
                .sorted((a, b) -> b - a) // Descending order
                .limit(lastNSprints)
                .collect(Collectors.toList());

        // Calculate total points per sprint
        int totalPoints = 0;
        int sprintCount = 0;

        for (Integer sprint : recentSprints) {
            int sprintPoints = sprintMap.get(sprint).stream()
                    .mapToInt(UserStory::storyPoints)
                    .sum();
            totalPoints += sprintPoints;
            sprintCount++;
            log.info("Sprint {}: {} points", sprint, sprintPoints);
        }

        double averageVelocity = sprintCount > 0 ? (double) totalPoints / sprintCount : 0;

        VelocityResult result = new VelocityResult(
                averageVelocity,
                sprintCount,
                totalPoints,
                recentSprints
        );

        log.info("Average velocity: {} points per sprint", averageVelocity);
        return result;
    }

    @Tool(description = "Analyze the complexity of a user story based on its description. " +
            "Returns a suggested story point estimate (1, 2, 3, 5, 8, 13) based on complexity indicators.")
    public ComplexityAnalysis analyzeStoryComplexity(
            @ToolParam(description = "The full description of the user story")
            String description) {

        log.info("Analyzing complexity for story: {}", description);

        String lowerDesc = description.toLowerCase();
        int complexityScore = 0;
        StringBuilder reasoning = new StringBuilder();

        // Check for complexity indicators
        if (lowerDesc.contains("api") || lowerDesc.contains("endpoint") || lowerDesc.contains("rest")) {
            complexityScore += 2;
            reasoning.append("Contains API development. ");
        }

        if (lowerDesc.contains("database") || lowerDesc.contains("migration") || lowerDesc.contains("schema")) {
            complexityScore += 3;
            reasoning.append("Involves database changes. ");
        }

        if (lowerDesc.contains("integration") || lowerDesc.contains("third-party") || lowerDesc.contains("external")) {
            complexityScore += 4;
            reasoning.append("Requires external integration. ");
        }

        if (lowerDesc.contains("authentication") || lowerDesc.contains("security") || lowerDesc.contains("authorization")) {
            complexityScore += 5;
            reasoning.append("Security-related work. ");
        }

        if (lowerDesc.contains("ui") || lowerDesc.contains("frontend") || lowerDesc.contains("react") || lowerDesc.contains("html")) {
            complexityScore += 2;
            reasoning.append("Frontend work. ");
        }

        if (lowerDesc.contains("test") || lowerDesc.contains("junit") || lowerDesc.contains("testing")) {
            complexityScore += 1;
            reasoning.append("Includes testing. ");
        }

        if (lowerDesc.contains("refactor") || lowerDesc.contains("cleanup") || lowerDesc.contains("improve")) {
            complexityScore += 2;
            reasoning.append("Code refactoring involved. ");
        }

        // Map score to Fibonacci scale
        int suggestedPoints;
        if (complexityScore <= 2) {
            suggestedPoints = 1;
            reasoning.append("Simple task.");
        } else if (complexityScore <= 4) {
            suggestedPoints = 2;
            reasoning.append("Straightforward implementation.");
        } else if (complexityScore <= 6) {
            suggestedPoints = 3;
            reasoning.append("Moderate complexity.");
        } else if (complexityScore <= 9) {
            suggestedPoints = 5;
            reasoning.append("Moderately complex.");
        } else if (complexityScore <= 12) {
            suggestedPoints = 8;
            reasoning.append("Complex task.");
        } else {
            suggestedPoints = 13;
            reasoning.append("Very complex, consider breaking down.");
        }

        ComplexityAnalysis analysis = new ComplexityAnalysis(
                complexityScore,
                suggestedPoints,
                reasoning.toString()
        );

        log.info("Complexity analysis: {} points (score: {})", suggestedPoints, complexityScore);
        return analysis;
    }

    @Tool(description = "Check for dependencies between user stories. " +
            "Identifies if a story depends on other stories being completed first.")
    public DependencyCheck checkDependencies(
            @ToolParam(description = "The ID of the story to check dependencies for")
            Long storyId) {

        log.info("Checking dependencies for story ID: {}", storyId);

        UserStory story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found: " + storyId));

        // Simple dependency detection based on description keywords
        String description = story.description().toLowerCase();
        List<UserStory> potentialDependencies = storyRepository.findByStatusAndSprintNumberIsNull("TODO");

        List<UserStory> dependencies = potentialDependencies.stream()
                .filter(s -> !s.id().equals(storyId))
                .filter(s -> {
                    String otherDesc = s.description().toLowerCase();
                    // Check if this story mentions the other story's key terms
                    return description.contains("after") &&
                            (description.contains(s.title().toLowerCase()) ||
                                    hasCommonKeywords(description, otherDesc));
                })
                .collect(Collectors.toList());

        boolean hasDependencies = !dependencies.isEmpty();
        String message = hasDependencies
                ? "Story has " + dependencies.size() + " potential dependencies. Complete those first."
                : "No blocking dependencies detected.";

        DependencyCheck check = new DependencyCheck(
                hasDependencies,
                dependencies,
                message
        );

        log.info("Dependency check: {}", message);
        return check;
    }

    @Tool(description = "Recommend stories for the upcoming sprint based on team velocity, story priority, " +
            "and complexity. Returns a prioritized list of stories that fit within the team's capacity.")
    public SprintRecommendation recommendStoriesForSprint(
            @ToolParam(description = "Target velocity (total story points) for the sprint")
            int targetVelocity) {

        log.info("Recommending stories for sprint with target velocity: {}", targetVelocity);

        // Fetch all backlog stories
        List<UserStory> backlog = storyRepository.findByStatusAndSprintNumberIsNull("TODO");

        // Sort by priority (HIGH first) and estimated points
        List<UserStory> sortedBacklog = backlog.stream()
                .sorted((a, b) -> {
                    // First by priority
                    int priorityCompare = comparePriority(b.priority(), a.priority());
                    if (priorityCompare != 0) return priorityCompare;
                    // Then by story points (prefer smaller stories for better flow)
                    return Integer.compare(a.storyPoints(), b.storyPoints());
                })
                .collect(Collectors.toList());

        // Select stories that fit within velocity
        List<UserStory> recommended = new java.util.ArrayList<>();
        int currentPoints = 0;

        for (UserStory story : sortedBacklog) {
            if (currentPoints + story.storyPoints() <= targetVelocity) {
                recommended.add(story);
                currentPoints += story.storyPoints();
            }

            // Stop when we've filled the sprint
            if (currentPoints >= targetVelocity * 0.9) { // 90% threshold
                break;
            }
        }

        SprintRecommendation recommendation = new SprintRecommendation(
                recommended,
                currentPoints,
                targetVelocity,
                targetVelocity - currentPoints
        );

        log.info("Recommended {} stories totaling {} points (capacity: {})",
                recommended.size(), currentPoints, targetVelocity);

        return recommendation;
    }

    private boolean hasCommonKeywords(String desc1, String desc2) {
        String[] words1 = desc1.split("\\s+");
        String[] words2 = desc2.split("\\s+");

        int commonCount = 0;
        for (String w1 : words1) {
            if (w1.length() > 4) { // Ignore short words
                for (String w2 : words2) {
                    if (w1.equals(w2)) {
                        commonCount++;
                    }
                }
            }
        }
        return commonCount >= 2;
    }

    private int comparePriority(String p1, String p2) {
        int val1 = priorityValue(p1);
        int val2 = priorityValue(p2);
        return Integer.compare(val1, val2);
    }

    private int priorityValue(String priority) {
        if (priority == null) return 0;
        switch (priority.toUpperCase()) {
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }
}