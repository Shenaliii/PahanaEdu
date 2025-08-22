/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.student.service.resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.Bill;
import dao.BillDAO;
import java.util.Date;
import java.util.List;
/**
 *
 * @author ASUS TUF
 */
@Path("bills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillResources {
    private final BillDAO utils = new BillDAO();
    private final Gson gson = new Gson();

    @POST
    public Response createBill(String billJson) {
        Bill bill = gson.fromJson(billJson, Bill.class);
        // Set bill date if not provided
        if (bill.getBillDate() == null) {
            bill.setBillDate(new Date());
        }
        boolean created = utils.createBill(bill);
        if (created) {
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(bill))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Bill could not be created\"}")
                    .build();
        }
    }
     // New endpoint to get last bill ID
    @GET
    @Path("lastid")
    public Response getLastBillId() {
        int lastId = utils.getLastBillId();
        JsonObject json = new JsonObject();
        json.addProperty("lastId", lastId);
        return Response.ok(json.toString()).build();
    }

   @GET
    public Response getBills() {
        // Return all bills JSON
        List<Bill> bills = utils.getBills();  // Make sure you have this method implemented
        String json = gson.toJson(bills);
        return Response.ok(json).build();
    }

    // A test endpoint to verify the service is up
    @GET
    @Path("test")
    public Response test() {
        return Response.ok("{\"message\":\"Bills resource is working\"}").build();
    }
    @GET
@Path("{id}")
public Response getBill(@PathParam("id") int id) {
    Bill bill = utils.getBillById(id);
    if (bill != null) {
        return Response.ok(gson.toJson(bill)).build();
    } else {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity("{\"error\":\"Bill not found\"}")
                       .build();
    }
}

}
