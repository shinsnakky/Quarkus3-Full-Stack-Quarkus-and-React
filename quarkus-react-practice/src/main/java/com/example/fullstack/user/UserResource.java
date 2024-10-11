package com.example.fullstack.user;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.ObjectNotFoundException;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/api/v1/users")
@RolesAllowed("admin")
public class UserResource {

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    public Uni<List<User>> get() {
        return User.listAll();
    }

    @GET
    @Path("{id}")
    public Uni<User> get(@PathParam("id") long id) {
        return User.<User>findById(id)
            .onItem().ifNull().failWith(
                () -> new ObjectNotFoundException(id, "User")
            );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<User> create(User user) {
        return User.create(user);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Uni<User> update(@PathParam("id") long id, User user) {
        user.id = id;
        return User.update(user);
    }

    @DELETE
    @Path("{id}")
    public Uni<Void> delete(@PathParam("id") long id) {
        return User.delete(id);
    }

    @GET
    @Path("self")
    @RolesAllowed("user")
    public Uni<User> getCurrentUser() {
        return User.findByName(jsonWebToken.getName());
    }

    @PUT
    @Path("self/password")
    @RolesAllowed("user")
    public Uni<User> changePassword(PasswordChange passwordChange) {
        return User
            .changePassword(
                passwordChange.currentPassword(),
                passwordChange.newPassword(),
                jsonWebToken.getName()
            );
    }
}
