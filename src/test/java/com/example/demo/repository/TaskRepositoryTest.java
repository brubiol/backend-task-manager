package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository layer integration tests.
 *
 * @DataJpaTest - Loads only JPA components (H2 in-memory DB, auto-rollback)
 */
@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(Task.TaskStatus.TODO);
        task1.setPriority(Task.TaskPriority.HIGH);
        task1.setAssignee("john");
        task1.setDueDate(LocalDateTime.now().plusDays(1));

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);
        task2.setPriority(Task.TaskPriority.MEDIUM);
        task2.setAssignee("jane");
        task2.setDueDate(LocalDateTime.now().plusDays(2));

        task3 = new Task();
        task3.setTitle("Task 3");
        task3.setDescription("Description 3");
        task3.setStatus(Task.TaskStatus.TODO);
        task3.setPriority(Task.TaskPriority.LOW);
        task3.setAssignee("john");
        task3.setDueDate(LocalDateTime.now().minusDays(1)); // Overdue

        taskRepository.saveAll(List.of(task1, task2, task3));
    }

    @Test
    void findByStatus_ReturnsTasksWithStatus() {
        List<Task> todoTasks = taskRepository.findByStatus(Task.TaskStatus.TODO);
        assertThat(todoTasks).hasSize(2);
        assertThat(todoTasks).extracting(Task::getStatus)
            .containsOnly(Task.TaskStatus.TODO);
    }

    @Test
    void findByStatusAndPriority_ReturnsFilteredTasks() {
        List<Task> tasks = taskRepository.findByStatusAndPriority(
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH);
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void findByAssignee_ReturnsTasksForAssignee() {
        List<Task> johnTasks = taskRepository.findByAssignee("john");
        assertThat(johnTasks).hasSize(2);
        assertThat(johnTasks).extracting(Task::getAssignee)
            .containsOnly("john");
    }

    @Test
    void findByStatus_WithPagination_ReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Task> page = taskRepository.findByStatus(Task.TaskStatus.TODO, pageable);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByDueDateBefore_ReturnsOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findByDueDateBefore(LocalDateTime.now());
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Task 3");
    }

    @Test
    void findByAssigneeIsNull_ReturnsUnassignedTasks() {
        Task unassignedTask = new Task();
        unassignedTask.setTitle("Unassigned Task");
        unassignedTask.setStatus(Task.TaskStatus.TODO);
        unassignedTask.setPriority(Task.TaskPriority.MEDIUM);
        unassignedTask.setAssignee(null);
        taskRepository.save(unassignedTask);

        List<Task> unassignedTasks = taskRepository.findByAssigneeIsNull();
        assertThat(unassignedTasks).hasSize(1);
        assertThat(unassignedTasks.get(0).getAssignee()).isNull();
    }

    @Test
    void findByTitleContainingIgnoreCase_ReturnsMatchingTasks() {
        List<Task> tasks = taskRepository.findByTitleContainingIgnoreCase("task");
        assertThat(tasks).hasSize(3);
    }

    @Test
    void findOverdueTasks_ReturnsTasksOverdueAndNotDone() {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Task 3");
    }

    @Test
    void countTasksByStatus_ReturnsGroupedCounts() {
        List<Object[]> results = taskRepository.countTasksByStatus();
        assertThat(results).isNotEmpty();
        for (Object[] row : results) {
            assertThat(row).hasSize(2);
            assertThat(row[0]).isInstanceOf(String.class);
            assertThat(row[1]).isInstanceOf(Number.class);
        }
    }

    @Test
    void existsByAssignee_ReturnsTrueIfTasksExist() {
        boolean exists = taskRepository.existsByAssignee("john");
        assertThat(exists).isTrue();
    }

    @Test
    void countByStatus_ReturnsCorrectCount() {
        long count = taskRepository.countByStatus(Task.TaskStatus.TODO);
        assertThat(count).isEqualTo(2);
    }
}
