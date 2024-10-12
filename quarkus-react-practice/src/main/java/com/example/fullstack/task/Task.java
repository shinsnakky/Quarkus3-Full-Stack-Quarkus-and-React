package com.example.fullstack.task;

import com.example.fullstack.project.Project;
import com.example.fullstack.user.User;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(length = 1000)
    public String description;

    public Integer priority;

    @ManyToOne(optional = false)
    public User user;

    public ZonedDateTime complete;

    @ManyToOne
    public Project project;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime created;

    @Version
    public int version;

    public static Uni<List<Task>> listForUser(String name) {
        return User.findByName(name)
                .chain(user -> Task.find("user", user).list());
    }

    @WithTransaction
    public static Uni<Task> create(Task task, String name) {
        return User.findByName(name)
                .chain(user -> {
                    task.user = user;
                    return task.persistAndFlush();
                });
    }

    @WithTransaction
    public static Uni<Task> update(Task task, String user) {
        return User.findByName(user)
            .chain(u -> Task.<Task>findById(task.id)
                .onItem().ifNull().failWith(
                    () -> new ObjectNotFoundException(task.id, "Task")
                )
                .onItem().invoke(t -> {
                    if (!u.equals(t.user)) {
                        throw new UnauthorizedException(
                            "You are not allowed to update this task"
                        );
                    }
                })
            ).chain(t -> Task.getSession())
            .chain(s -> s.merge(task));
    }

    @WithTransaction
    public static Uni<Void> delete(long id) {
        return Task.<Task>findById(id)
                .chain(Task::delete);
    }

    @WithTransaction
    public static Uni<Boolean> setComplete(long id, boolean complete) {
        return Task.<Task>findById(id)
                .chain(task -> {
                    task.complete = complete ? ZonedDateTime.now() : null;
                    return task.persistAndFlush();
                })
                .chain(task -> Uni.createFrom().item(complete));
    }
}
