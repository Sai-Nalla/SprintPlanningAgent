package com.sai.ai_chatbot.repository;

import com.sai.ai_chatbot.model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<UserStory, Long> {

    List<UserStory> findByStatus(String status);

    List<UserStory> findByStatusAndSprintNumberIsNull(String status);

    List<UserStory> findBySprintNumber(Integer sprintNumber);

    List<UserStory> findByPriority(String priority);
}