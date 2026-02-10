package com.example.demo.dto;

import com.example.demo.entity.Task;

/**
 * DTO for reporting/aggregation results.
 *
 * This is what you return from GROUP BY queries.
 * Maps to the Object[] results from native queries.
 */
public class TaskStatusReport {
    private Task.TaskStatus status;
    private Long count;

    public TaskStatusReport() {
    }

    public TaskStatusReport(Task.TaskStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
