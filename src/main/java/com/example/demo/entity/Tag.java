package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Tag entity — demonstrates @ManyToMany relationship.
 *
 * Tags and Tasks have a many-to-many relationship:
 * - A Task can have many Tags
 * - A Tag can be applied to many Tasks
 *
 * KEY CONCEPTS:
 * - @ManyToMany(mappedBy) — the inverse side doesn't own the join table
 * - The owning side (Task) defines @JoinTable
 */
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * mappedBy = "tags" — refers to the 'tags' field in Task entity.
     * This is the INVERSE side — Task owns the relationship.
     */
    @ManyToMany(mappedBy = "tags")
    private Set<Task> tasks = new HashSet<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
