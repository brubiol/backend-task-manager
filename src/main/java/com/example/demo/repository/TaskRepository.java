package com.example.demo.repository;

import com.example.demo.entity.Task;
import com.example.demo.entity.Task.TaskPriority;
import com.example.demo.entity.Task.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository layer — SQL thinking meets Spring Data JPA.
 *
 * KEY CONCEPTS:
 * 1. Derived Query Methods: Spring generates SQL from method names
 * 2. JPQL: Java Persistence Query Language (entity-based, not table-based)
 * 3. Native SQL: Raw SQL when you need full control
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ===== SOFT-DELETE AWARE QUERIES =====

    Optional<Task> findByIdAndDeletedFalse(Long id);

    /**
     * JOIN FETCH to prevent N+1 queries when loading a task with its tags and comments.
     */
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags LEFT JOIN FETCH t.comments WHERE t.id = :id AND t.deleted = false")
    Optional<Task> findByIdWithRelationships(@Param("id") Long id);

    Page<Task> findByDeletedFalse(Pageable pageable);

    Page<Task> findByStatusAndDeletedFalse(TaskStatus status, Pageable pageable);

    // ===== DERIVED QUERY METHODS =====

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);

    List<Task> findByAssignee(String assignee);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    List<Task> findByDueDateBefore(LocalDateTime date);

    List<Task> findByAssigneeIsNull();

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // ===== JPQL QUERIES =====

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:assignee IS NULL OR t.assignee = :assignee) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findTasksWithFilters(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("assignee") String assignee,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND t.dueDate < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    // ===== NATIVE SQL =====

    @Query(value = "SELECT status, COUNT(*) as count " +
                   "FROM tasks " +
                   "WHERE deleted = false " +
                   "GROUP BY status",
           nativeQuery = true)
    List<Object[]> countTasksByStatus();

    // ===== EXISTENCE CHECKS =====

    boolean existsByAssignee(String assignee);

    long countByStatus(TaskStatus status);
}
