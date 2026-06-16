package com.sai.ai_chatbot.service;

import com.sai.ai_chatbot.config.SprintPlanningTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;


@Service
public class SprintPlanningService {

    private static final Logger log = LoggerFactory.getLogger(SprintPlanningService.class);


    private final ChatModel chatModel;
    private final SprintPlanningTools sprintPlanningTools;

    public SprintPlanningService(ChatModel chatModel,
                                 SprintPlanningTools sprintPlanningTools) {
        this.chatModel = chatModel;
        this.sprintPlanningTools = sprintPlanningTools;
    }

    private static final String SPRINT_PLANNING_AGENT_PROMPT = """
            You are an expert Agile Sprint Planning Assistant. Your role is to help teams plan their sprints effectively.
            
            You have access to these tools:
            1. fetchBacklogStories - Get all unassigned stories from the backlog
            2. findSimilarStories - Find similar completed stories to help estimate new ones
            3. getTeamVelocity - Calculate team's average velocity from past sprints
            4. analyzeStoryComplexity - Analyze complexity and suggest story points
            5. checkDependencies - Check if a story has dependencies on other stories
            6. recommendStoriesForSprint - Get recommended stories that fit team capacity
            
            WORKFLOW:
            1. If user asks to "plan sprint" or "recommend stories":
               - First, call getTeamVelocity(3) to get average velocity
               - Then call recommendStoriesForSprint with that velocity
               - Present the recommendations with reasoning
            
            2. If user asks to "estimate story points" for a specific story:
               - Call analyzeStoryComplexity with the story description
               - Call findSimilarStories to compare with past stories
               - Provide estimation with reasoning
            
            3. If user asks about "dependencies":
               - Call checkDependencies for the story
               - Explain any blocking dependencies
            
            4. If user asks about "team velocity":
               - Call getTeamVelocity with last 3-5 sprints
               - Explain the trend
            
            IMPORTANT RULES:
            - Always explain your reasoning
            - Use tool results to back up your recommendations
            - Be specific about story points (use Fibonacci: 1, 2, 3, 5, 8, 13)
            - Warn if sprint seems overcommitted (velocity exceeded by >10%)
            - Suggest breaking down stories if complexity is 13 points
            - Consider dependencies when recommending stories
            
            Be concise but thorough. Focus on actionable insights.
            """;

    public String planSprint(String userQuery) {
        log.info("Processing sprint planning query: {}", userQuery);

        try {
            ChatClient chatClient = ChatClient.builder(chatModel).build();

            String response = chatClient.prompt()
                    .system(SPRINT_PLANNING_AGENT_PROMPT)
                    .user(userQuery)
                    .tools(sprintPlanningTools)
                    .call()
                    .content();

            log.info("Sprint planning response generated successfully");
            return response;

        } catch (Exception e) {
            log.error("Error in sprint planning: {}", e.getMessage(), e);
            return "Error processing sprint planning request: " + e.getMessage();
        }
    }
}