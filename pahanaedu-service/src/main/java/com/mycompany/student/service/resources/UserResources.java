package com.mycompany.student.service.resources;

import com.google.gson.Gson;
import dao.UserDAO;
import models.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {

    private final UserDAO dao = new UserDAO();
    private final Gson gson = new Gson();

    @GET
    public Response getUsers() {
        List<User> users = dao.getAllUsers();
        return Response.ok(gson.toJson(users)).build();
    }

    @POST
    public Response createUser(String userJson) {
        User u = gson.fromJson(userJson, User.class);
        boolean created = dao.createUser(u);
        if (created) return Response.status(Response.Status.CREATED).entity(gson.toJson(u)).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Cannot create user\"}").build();
    }

    @PUT
    @Path("{username}")
    public Response updateUser(@PathParam("username") String username, String userJson) {
        User u = gson.fromJson(userJson, User.class);
        u.setUsername(username);
        boolean updated = dao.updateUser(u);
        if (updated) return Response.ok(gson.toJson(u)).build();
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"User not found\"}").build();
    }

    @DELETE
    @Path("{username}")
    public Response deleteUser(@PathParam("username") String username) {
        boolean deleted = dao.deleteUser(username);
        if (deleted) return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"User not found\"}").build();
    }
}
