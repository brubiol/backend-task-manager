package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Tag;
import com.example.demo.entity.Task;
import com.example.demo.entity.Task.TaskPriority;
import com.example.demo.entity.Task.TaskStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer — all business logic lives here.
 *
 * PATTERN: Controller → Service → Repository
 *
 * KEY CONCEPTS:
 * - Constructor injection (preferred over @Autowired)
 * - @Transactional: readOnly=true at class level, write methods override
 * - Entity ↔ DTO conversion keeps internals out of API responses
 * - Soft delete: set deleted=true instead of actually removing rows
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    public TaskService(TaskRepository taskRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
    }

    // ===== CRUD =====

    @Transactional
    public TaskDTO createTask(CreateTaskRequest request) {
        log.info("Creating task: {}", request.getTitle());

        Task task = request.toEntity();

        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            task.setTags(resolveOrCreateTags(request.getTagNames()));
        }

        Task saved = taskRepository.save(task);
        log.info("Task created with id: {}", saved.getId());
        return TaskDTO.fromEntity(saved);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findByIdWithRelationships(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return TaskDTO.fromEntity(task);
    }

    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findByDeletedFalse(pageable).map(TaskDTO::fromEntity);
    }

    @Transactional
    public TaskDTO updateTask(Long id, UpdateTaskRequest request) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getAssignee() != null) task.setAssignee(request.getAssignee());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        Task updated = taskRepository.save(task);
        log.info("Task updated: {}", updated.getId());
        return TaskDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        log.info("Soft-deleting task with id: {}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        task.setDeleted(true);
        taskRepository.save(task);
        log.info("Task soft-deleted: {}", id);
    }

    // ===== FILTERING =====

    public Page<TaskDTO> getTasksByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatusAndDeletedFalse(status, pageable).map(TaskDTO::fromEntity);
    }

    public Page<TaskDTO> getTasksWithFilters(TaskStatus status, TaskPriority priority,
                                              String assignee, Pageable pageable) {
        return taskRepository.findTasksWithFilters(status, priority, assignee, pageable)
            .map(TaskDTO::fromEntity);
    }

    public List<TaskDTO> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
            .map(TaskDTO::fromEntity).collect(Collectors.toList());
    }

    // ===== REPORTING (native SQL) =====

    public List<TaskStatusReport> getTaskCountByStatus() {
        return taskRepository.countTasksByStatus().stream()
            .map(row -> new TaskStatusReport(
                TaskStatus.valueOf((String) row[0]),
                ((Number) row[1]).longValue()))
            .collect(Collectors.toList());
    }

    // ===== HELPERS =====

    private Set<Tag> resolveOrCreateTags(Set<String> tagNames) {
        Set<Tag> existing = tagRepository.findByNameIn(tagNames);
        Set<String> existingNames = existing.stream().map(Tag::getName).collect(Collectors.toSet());
        Set<Tag> all = new HashSet<>(existing);
        for (String name : tagNames) {
            if (!existingNames.contains(name)) {
                all.add(tagRepository.save(new Tag(name)));
            }
        }
        return all;
    }
}
