package com.sai.ai_chatbot.config;

import com.sai.ai_chatbot.model.UserStory;
import com.sai.ai_chatbot.repository.StoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SprintDataLoader implements CommandLineRunner {

    private final StoryRepository storyRepository;

    public SprintDataLoader(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (storyRepository.count() > 0) {
            System.out.println("Database already has " + storyRepository.count() + " stories. Skipping sample data load.");
            return;
        }

        System.out.println("Loading sample user stories into database...");
        loadSampleData();

        long total = storyRepository.count();
        long backlog = storyRepository.findByStatus("TODO").size();
        long sprint1 = storyRepository.findBySprintNumber(1).size();
        long sprint2 = storyRepository.findBySprintNumber(2).size();

        System.out.println("Loaded " + total + " user stories into database");
        System.out.println("- Backlog: " + backlog + " stories");
        System.out.println("- Sprint 1: " + sprint1 + " stories");
        System.out.println("- Sprint 2: " + sprint2 + " stories");
    }

    // PUBLIC method so SprintPlanningController can reuse it
    public void loadSampleData() {
        // ========== BACKLOG STORIES (TODO) ==========
        storyRepository.save(new UserStory(
                null,
                "User Login",
                "Implement user authentication with JWT tokens",
                "TODO",
                "HIGH",
                3,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Password Reset",
                "Email-based password reset functionality",
                "TODO",
                "MEDIUM",
                5,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "User Profile Page",
                "Display and edit user profile information",
                "TODO",
                "MEDIUM",
                5,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Product Search API",
                "Search products by keywords with filters",
                "TODO",
                "HIGH",
                8,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Shopping Cart",
                "Add/remove items from shopping cart",
                "TODO",
                "HIGH",
                8,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Payment Integration",
                "Integrate Stripe payment gateway",
                "TODO",
                "HIGH",
                13,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Order History",
                "Display past orders with tracking",
                "TODO",
                "LOW",
                3,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Email Notifications",
                "Send order confirmation and shipping emails",
                "TODO",
                "MEDIUM",
                5,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Product Reviews",
                "Allow users to review and rate products",
                "TODO",
                "LOW",
                8,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        storyRepository.save(new UserStory(
                null,
                "Admin Dashboard",
                "Analytics dashboard for administrators",
                "TODO",
                "MEDIUM",
                13,
                null,
                LocalDateTime.now(),
                null,
                null
        ));

        // ========== SPRINT 1 COMPLETED STORIES ==========
        LocalDateTime sprint1Start = LocalDateTime.now().minusDays(30);
        LocalDateTime sprint1End = LocalDateTime.now().minusDays(16);

        storyRepository.save(new UserStory(
                null,
                "Database Schema",
                "Design and create database schema",
                "DONE",
                "HIGH",
                5,
                1,
                sprint1Start,
                sprint1End,
                "Alice"
        ));

        storyRepository.save(new UserStory(
                null,
                "API Setup",
                "Setup Spring Boot REST API structure",
                "DONE",
                "HIGH",
                8,
                1,
                sprint1Start,
                sprint1End,
                "Bob"
        ));

        storyRepository.save(new UserStory(
                null,
                "Authentication Service",
                "JWT-based authentication service",
                "DONE",
                "HIGH",
                5,
                1,
                sprint1Start,
                sprint1End,
                "Charlie"
        ));

        // ========== SPRINT 2 COMPLETED STORIES ==========
        LocalDateTime sprint2Start = LocalDateTime.now().minusDays(15);
        LocalDateTime sprint2End = LocalDateTime.now().minusDays(1);

        storyRepository.save(new UserStory(
                null,
                "Product Listing UI",
                "Display products in responsive grid",
                "DONE",
                "MEDIUM",
                3,
                2,
                sprint2Start,
                sprint2End,
                "Alice"
        ));

        storyRepository.save(new UserStory(
                null,
                "Product Details Page",
                "Show detailed product information",
                "DONE",
                "MEDIUM",
                5,
                2,
                sprint2Start,
                sprint2End,
                "Bob"
        ));

        storyRepository.save(new UserStory(
                null,
                "Add to Cart Functionality",
                "Basic cart add/remove operations",
                "DONE",
                "HIGH",
                5,
                2,
                sprint2Start,
                sprint2End,
                "Charlie"
        ));

        storyRepository.save(new UserStory(
                null,
                "Cart UI",
                "Shopping cart page design and layout",
                "DONE",
                "MEDIUM",
                3,
                2,
                sprint2Start,
                sprint2End,
                "Alice"
        ));

        storyRepository.save(new UserStory(
                null,
                "Checkout Flow",
                "Multi-step checkout process",
                "DONE",
                "HIGH",
                8,
                2,
                sprint2Start,
                sprint2End,
                "Bob"
        ));
    }
}