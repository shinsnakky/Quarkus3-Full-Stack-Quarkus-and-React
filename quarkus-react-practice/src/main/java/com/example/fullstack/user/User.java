package com.example.fullstack.user;

import com.example.fullstack.project.Project;
import com.example.fullstack.task.Task;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public String name;

    @Column(nullable = false)
    String password;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime created;

    @Version
    public int version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "role")
    public List<String> roles;

    // Setter is needed to deserialize HTTP request when creating a user
    // with POST since password field of User class is package private.
    // However, @JsonProperty("password") is not necessarily required here.
    // "password" property of JSON objects maps to the corresponding field
    // through the function name.
    public void setPassword(String password) {
        this.password = password;
    }

    public static Uni<User> findByName(String name) {
        return find("name", name).firstResult();
    }

    @WithTransaction
    public static Uni<User> create(User user) {
        // If request JSON object does not have password property,
        // user.password will be set to be Null, then bcryptHash
        // throws NullPointerException 
        if (user.password != null) {
            user.password = BcryptUtil.bcryptHash(user.password);
        }
        return user.persistAndFlush();
    }

    @WithTransaction
    public static Uni<User> update(User user) {
        return User.<User>findById(user.id)
            .chain(u -> {
                user.setPassword(u.password);
                return User.getSession();
            })
            .chain(s -> s.merge(user));
    }

    @WithTransaction
    public static Uni<Void> delete(long id) {
        return User.<User>findById(id)
            .chain(
                u -> Uni.combine().all().unis(
                    Task.delete("user.id", u.id),
                    Project.delete("user.id", u.id)
                ).asTuple().chain(t -> u.delete())
            );
    }

    public static boolean matches(User user, String password) {
        return BcryptUtil.matches(password, user.password);
    }

    @WithTransaction
    public static Uni<User> changePassword(
            String currentPassword,
            String newPassword,
            String name
    ) {
        return findByName(name)
                .chain(u -> {
                    if (!matches(u, currentPassword)) {
                        throw new ClientErrorException(
                                "Current password does not match",
                                Response.Status.CONFLICT
                        );
                    }
                    u.setPassword(BcryptUtil.bcryptHash(newPassword));
                    return u.persistAndFlush();
                });
    }
}
