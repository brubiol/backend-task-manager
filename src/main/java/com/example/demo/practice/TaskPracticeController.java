package com.example.demo.practice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Practice controller for task endpoints.
 */
@RestController
@RequestMapping("/practice/tasks")
public class TaskPracticeController {

    private final TaskPracticeService taskPracticeService;

    public TaskPracticeController(TaskPracticeService taskPracticeService) {
        this.taskPracticeService = taskPracticeService;
    }

    @GetMapping("/dummy")
    public ResponseEntity<TaskPracticeResponse> getDummyTask() {
        return ResponseEntity.ok(taskPracticeService.getDummyTask());
    }
}
