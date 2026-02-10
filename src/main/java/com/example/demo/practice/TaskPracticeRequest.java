package com.example.demo.practice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Practice request DTO for task input.
 */
public class TaskPracticeRequest {

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be 1-200 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    public TaskPracticeRequest() {
    }

    public TaskPracticeRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
