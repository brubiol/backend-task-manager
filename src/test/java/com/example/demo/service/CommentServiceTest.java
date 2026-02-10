package com.example.demo.service;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CreateCommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Task;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_ValidRequest_ReturnsCommentDTO() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(Task.TaskStatus.TODO);
        task.setPriority(Task.TaskPriority.HIGH);

        when(taskRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(task));

        Comment saved = new Comment();
        saved.setId(1L);
        saved.setContent("Great work!");
        saved.setAuthor("alice");
        saved.setTask(task);
        saved.setCreatedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CreateCommentRequest request = new CreateCommentRequest("Great work!", "alice");
        CommentDTO result = commentService.addComment(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Great work!");
        assertThat(result.getTaskId()).isEqualTo(1L);
    }

    @Test
    void addComment_TaskNotFound_ThrowsException() {
        when(taskRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        CreateCommentRequest request = new CreateCommentRequest("Comment", "alice");

        assertThatThrownBy(() -> commentService.addComment(99L, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
