package com.sai.ai_chatbot.model;

public record ComplexityAnalysis (
    int complexityScore,
    int estimatedPoints,
    String reasoning
){}