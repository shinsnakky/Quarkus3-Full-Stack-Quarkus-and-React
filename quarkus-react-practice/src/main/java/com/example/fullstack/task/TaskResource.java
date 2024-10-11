package com.example.fullstack.task;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Path("/api/v1/tasks")
@RolesAllowed("user")
public class TaskResource {

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    public Uni<List<Task>> get() {
        return Task.listForUser(jsonWebToken.getName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<Task> create(Task task) {
        return Task.create(task, jsonWebToken.getName());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Uni<Task> update(@PathParam("id") long id, Task task) {
        task.id = id;
        return Task.update(task);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") long id) {
        return Task.delete(id);
    }

    @PUT
    @Path("/{id}/complete")
    public Uni<Boolean> setComplete(@PathParam("id") long id, boolean complete) {
        return Task.setComplete(id, complete);
    }

}