package com.sai.ai_chatbot.model;

import java.util.List;

public record DependencyCheck (
     boolean hasDependencies,
     List<UserStory> blockingStories,
     String recommendation
){}