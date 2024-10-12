package com.example.fullstack.project;

import com.example.fullstack.task.Task;
import com.example.fullstack.user.User;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(
    name = "projects",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_id"})
    }
)
public class Project extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @ManyToOne(optional = false)
    public User user;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime created;

    @Version
    public int version;

    public static Uni<List<Project>> listForUser(String name) {
        return User.findByName(name)
                .chain(user -> Project.find("user", user).list());
    }

    @WithTransaction
    public static Uni<Project> create(Project project, String name) {
        return User.findByName(name)
                .chain(user -> {
                    project.user = user;
                    return project.persistAndFlush();
                });
    }

    //
    @WithTransaction
    public static Uni<Project> update(Project project, String user) {
        /*
        return Project.<Project>findById(project.id)
            .onItem().ifNull().failWith(
                () -> new ObjectNotFoundException(project.id, "Project")
            )
            .chain(p -> Project.getSession())
            .chain(s -> s.merge(project));
         */
        return User.findByName(user)
            .chain(u -> Project.<Project>findById(project.id)
                .onItem().ifNull().failWith(() -> new ObjectNotFoundException(project.id, "Project"))
                .onItem().invoke(p -> {
                    if (!u.equals(p.user)) {
                        throw new UnauthorizedException("You are not allowed to update this project");
                    }
                })
            ).chain(p -> Project.getSession())
            .chain(s -> s.merge(project));
    }

    @WithTransaction
    public static Uni<Void> delete(long id) {
        return Project.<Project>findById(id)
            .chain(
                p -> Task.update("project = null where project = ?1", p)
                    .chain(i -> p.delete())
            );
    }
}
