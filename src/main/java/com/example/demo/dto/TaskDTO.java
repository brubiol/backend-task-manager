package com.example.demo.dto;

import com.example.demo.entity.Tag;
import com.example.demo.entity.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for Task responses.
 *
 * WHY USE DTOs?
 * - Decouple API response from database entity
 * - Control what data is exposed to clients
 * - Prevent exposing internal entity structure
 * - Add computed fields without modifying entity
 */
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private String assignee;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed field - not in database
    private boolean overdue;

    // Relationship fields
    private List<CommentDTO> comments = new ArrayList<>();
    private Set<String> tagNames;

    // Constructors
    public TaskDTO() {
    }

    public TaskDTO(Long id, String title, String description, Task.TaskStatus status, Task.TaskPriority priority,
                   String assignee, LocalDateTime dueDate, LocalDateTime createdAt, LocalDateTime updatedAt, boolean overdue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.overdue = overdue;
    }

    /**
     * Convert Entity → DTO
     * This is the pattern you'll use in every service method
     */
    public static TaskDTO fromEntity(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setAssignee(task.getAssignee());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        // Computed field
        dto.setOverdue(task.getDueDate() != null &&
                       task.getDueDate().isBefore(LocalDateTime.now()) &&
                       task.getStatus() != Task.TaskStatus.DONE);

        // Relationships
        if (task.getComments() != null) {
            dto.setComments(task.getComments().stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList()));
        }
        if (task.getTags() != null) {
            dto.setTagNames(task.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet()));
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }

    public Task.TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(Task.TaskPriority priority) {
        this.priority = priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }
}
