package com.example.demo.practice;

import org.springframework.stereotype.Service;

/**
 * Practice service for task-related stubs.
 */
@Service
public class TaskPracticeService {

    public TaskPracticeResponse getDummyTask() {
        TaskPracticeResponse response = new TaskPracticeResponse();
        response.setId(1L);
        response.setTitle("Practice Task");
        response.setDescription("Dummy response for practice.");
        return response;
    }
}
