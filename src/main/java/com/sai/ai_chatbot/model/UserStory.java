package com.sai.ai_chatbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_stories")
public class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String priority;

    private Integer storyPoints;
    private Integer sprintNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private String assignee;

    // No-arg constructor (required by JPA)
    public UserStory() {
    }

    // All-args constructor (for sample data)
    public UserStory(Long id, String title, String description, String status,
                     String priority, Integer storyPoints, Integer sprintNumber,
                     LocalDateTime createdAt, LocalDateTime completedAt, String assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.storyPoints = storyPoints;
        this.sprintNumber = sprintNumber;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.assignee = assignee;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ===== RECORD-STYLE GETTERS (for your service code) =====
    public Long id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public String status() { return status; }
    public String priority() { return priority; }
    public Integer storyPoints() { return storyPoints; }
    public Integer sprintNumber() { return sprintNumber; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime completedAt() { return completedAt; }
    public String assignee() { return assignee; }

    // ===== JAVABEAN-STYLE GETTERS (for JSON serialization) =====
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public Integer getStoryPoints() { return storyPoints; }
    public Integer getSprintNumber() { return sprintNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getAssignee() { return assignee; }

    // ===== SETTERS (required by JPA) =====
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
    public void setSprintNumber(Integer sprintNumber) { this.sprintNumber = sprintNumber; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
}