/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author ASUS TUF
 */
public class Customer {
       public int id;
    private String name;
    private String email;
    private String address;
    private String mobile;
    private int unit_consumed;
    
     public Customer() {
    }

    public Customer(int id, String name, String email, String address, String mobile, int unit_consumed) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.mobile = mobile;
        this.unit_consumed = unit_consumed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getUnit_consumed() {
        return unit_consumed;
    }

    public void setUnit_consumed(int unit_consumed) {
        this.unit_consumed = unit_consumed;
    }
     
}
