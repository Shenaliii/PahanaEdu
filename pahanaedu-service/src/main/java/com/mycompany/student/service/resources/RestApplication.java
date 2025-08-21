/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.student.service.resources;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
/**
 *
 * @author ASUS TUF
 */
@ApplicationPath("/")  // No prefix; URLs will be http://host:port/yourapp/<resource>
public class RestApplication extends Application {
    // No methods needed here unless you want to customize behavior.
}
