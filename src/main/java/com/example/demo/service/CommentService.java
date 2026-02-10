package com.example.demo.service;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CreateCommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Task;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public CommentDTO addComment(Long taskId, CreateCommentRequest request) {
        log.info("Adding comment to task: {}", taskId);

        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(request.getAuthor());
        comment.setTask(task);

        Comment saved = commentRepository.save(comment);
        return CommentDTO.fromEntity(saved);
    }

    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        log.debug("Fetching comments for task: {}", taskId);

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }

        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
            .stream()
            .map(CommentDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment: {}", commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment", commentId);
        }
        commentRepository.deleteById(commentId);
    }
}
