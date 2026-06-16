package com.sai.ai_chatbot.model;

import java.util.List;

public record SprintRecommendation (
     List<UserStory> recommendedStories,
     int totalPoints,
     int targetVelocity,
     int remainingCapacity
){}