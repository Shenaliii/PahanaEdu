/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.student.service.resources;
import com.google.gson.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import dao.UserDAO;
import models.User;
/**
 *
 * @author ASUS TUF
 */
@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResources {
    private final UserDAO utils = new UserDAO();

    @POST
    public Response login(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();

        User user = utils.authenticateUser(username, password);
        if (user != null) {
            JsonObject response = new JsonObject();
            response.addProperty("message", "Login successful");
            response.addProperty("token", "dummy-token"); // replace with JWT if needed
            response.addProperty("role", user.getRole());   // return role
            return Response.ok(response.toString()).build();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("error", "Invalid username or password");
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(response.toString())
                           .build();
        }
    }
}