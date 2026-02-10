package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskDTO;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.entity.Task;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TagRepository;
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
import static org.mockito.Mockito.*;

/**
 * Service layer tests with Mockito.
 *
 * @ExtendWith(MockitoExtension.class) — enables @Mock and @InjectMocks
 * @Mock — creates a mock of the repository
 * @InjectMocks — creates the service and injects all mocks
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_ValidRequest_ReturnsTaskDTO() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task", "Description",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", LocalDateTime.now().plusDays(1)
        );

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Description");
        savedTask.setStatus(Task.TaskStatus.TODO);
        savedTask.setPriority(Task.TaskPriority.HIGH);
        savedTask.setAssignee("john");
        savedTask.setCreatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_ExistingTask_ReturnsTaskDTO() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(Task.TaskStatus.TODO);
        task.setPriority(Task.TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());

        when(taskRepository.findByIdWithRelationships(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findByIdWithRelationships(1L);
    }

    @Test
    void getTaskById_NonExistingTask_ThrowsException() {
        when(taskRepository.findByIdWithRelationships(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Task not found with id: 1");
    }

    @Test
    void updateTask_ExistingTask_ReturnsUpdatedTaskDTO() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setStatus(Task.TaskStatus.TODO);
        existingTask.setPriority(Task.TaskPriority.LOW);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("New Title");
        request.setStatus(Task.TaskStatus.IN_PROGRESS);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("New Title");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(Task.TaskPriority.LOW);
        updatedTask.setCreatedAt(LocalDateTime.now());

        when(taskRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDTO result = taskService.updateTask(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getStatus()).isEqualTo(Task.TaskStatus.IN_PROGRESS);
        verify(taskRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ExistingTask_SoftDeletes() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Task to delete");
        existingTask.setStatus(Task.TaskStatus.TODO);
        existingTask.setPriority(Task.TaskPriority.HIGH);

        when(taskRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void deleteTask_NonExistingTask_ThrowsException() {
        when(taskRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }
}
