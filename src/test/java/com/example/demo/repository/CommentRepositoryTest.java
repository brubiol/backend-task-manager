package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        taskRepository.deleteAll();

        task = new Task();
        task.setTitle("Test Task");
        task.setStatus(Task.TaskStatus.TODO);
        task.setPriority(Task.TaskPriority.HIGH);
        task = taskRepository.save(task);

        Comment c1 = new Comment();
        c1.setContent("First comment");
        c1.setAuthor("alice");
        c1.setTask(task);
        commentRepository.save(c1);

        Comment c2 = new Comment();
        c2.setContent("Second comment");
        c2.setAuthor("bob");
        c2.setTask(task);
        commentRepository.save(c2);
    }

    @Test
    void findByTaskId_ReturnsCommentsOrderedByCreatedAtDesc() {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(task.getId());
        assertThat(comments).hasSize(2);
    }
}
