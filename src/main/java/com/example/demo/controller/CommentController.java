package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CreateCommentRequest;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Nested REST controller for Task Comments.
 *
 * Demonstrates sub-resource routing pattern:
 * POST   /api/tasks/{taskId}/comments     → Add comment
 * GET    /api/tasks/{taskId}/comments     → List comments
 * DELETE /api/tasks/{taskId}/comments/{id} → Delete comment
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("POST /api/tasks/{}/comments", taskId);
        CommentDTO comment = commentService.addComment(taskId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long taskId) {
        log.info("GET /api/tasks/{}/comments", taskId);
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        log.info("DELETE /api/tasks/{}/comments/{}", taskId, commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
