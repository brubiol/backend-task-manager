package com.example.demo.controller;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskDTO;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller layer tests.
 *
 * @WebMvcTest — loads only web layer (controllers, filters, etc.)
 * @AutoConfigureMockMvc(addFilters = false) — disables security filters for unit testing
 * @MockBean — mocks the service layer
 */
@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createTask_ValidRequest_Returns201() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task", "Description",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", LocalDateTime.now().plusDays(1)
        );

        TaskDTO responseDTO = new TaskDTO(
            1L, "Test Task", "Description",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", LocalDateTime.now().plusDays(1),
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void createTask_InvalidRequest_Returns400() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
            null, "Description",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", null
        );

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_ExistingTask_Returns200() throws Exception {
        TaskDTO taskDTO = new TaskDTO(
            1L, "Test Task", "Description",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", LocalDateTime.now(),
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        when(taskService.getTaskById(1L)).thenReturn(taskDTO);

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getAllTasks_Returns200WithPage() throws Exception {
        TaskDTO task1 = new TaskDTO(
            1L, "Task 1", "Desc 1",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            "john", null,
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        Page<TaskDTO> page = new PageImpl<>(List.of(task1));
        when(taskService.getAllTasks(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tasks")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].title").value("Task 1"));
    }

    @Test
    void updateTask_ValidRequest_Returns200() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        request.setStatus(Task.TaskStatus.IN_PROGRESS);

        TaskDTO updatedDTO = new TaskDTO(
            1L, "Updated Title", "Description",
            Task.TaskStatus.IN_PROGRESS, Task.TaskPriority.HIGH,
            "john", null,
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        when(taskService.updateTask(eq(1L), any(UpdateTaskRequest.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTask_ExistingTask_Returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void getTasksByStatus_Returns200() throws Exception {
        TaskDTO task1 = new TaskDTO(
            1L, "Task 1", "Desc",
            Task.TaskStatus.TODO, Task.TaskPriority.HIGH,
            null, null,
            LocalDateTime.now(), LocalDateTime.now(), false
        );

        Page<TaskDTO> page = new PageImpl<>(List.of(task1));
        when(taskService.getTasksByStatus(any(Task.TaskStatus.class), any(Pageable.class)))
            .thenReturn(page);

        mockMvc.perform(get("/api/tasks/status/TODO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].status").value("TODO"));
    }
}
