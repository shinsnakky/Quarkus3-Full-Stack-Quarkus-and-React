package com.example.fullstack.project;

import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/v1/projects")
@RolesAllowed("user")
public class ProjectResource {

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    public Uni<List<Project>> get() {
        return Project.listForUser(jsonWebToken.getName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<Project> create(Project project) {
        return Project.create(project, jsonWebToken.getName());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    // Projects are updatable only by the user who created it.
    public Uni<Project> update(@PathParam("id") long id, Project project) {
        project.id = id;
        //if (!jsonWebToken.getName().equals(project.user.name)) {
        //    throw new UnauthorizedException("You are not allowed to update this project");
        //}

        return Project.update(project, jsonWebToken.getName());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") long id) {
        return Project.delete(id);
    }

}
