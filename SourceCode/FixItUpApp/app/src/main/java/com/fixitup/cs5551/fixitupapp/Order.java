package com.fixitup.cs5551.fixitupapp;

public class Order {
    String technicianID;
    String customerID;
    public Order(){
        technicianID = "";
        customerID = "";
    }
    public Order(String tID, String cID) {
       technicianID = tID;
       customerID = cID;

    }
    public String getTechnicianID() {
        return technicianID;
    }

    public String getCustomerID() {
        return customerID;
    }
    
}
