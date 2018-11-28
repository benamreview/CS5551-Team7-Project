package com.fixitup.cs5551.fixitupapp;

public class Order {
    String technicianID;
    String customerID;
    String status;
    public Order(){
        technicianID = "";
        customerID = "";
        status = "";
    }
    public Order(String tID, String cID, String stat) {
       technicianID = tID;
       customerID = cID;
       status = stat;

    }
    public String getTechnicianID() {
        return technicianID;
    }

    public String getCustomerID() {
        return customerID;
    }
    public String getStatus(){
        return status;
    }

}
