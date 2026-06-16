package com.sai.ai_chatbot.model;

import java.util.List;

public record VelocityResult (
     double averageVelocity,
     int totalSprints,
     int totalPoints,
     List<Integer> pointsPerSprint
){}