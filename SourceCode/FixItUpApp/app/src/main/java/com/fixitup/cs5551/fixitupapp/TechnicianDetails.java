package com.fixitup.cs5551.fixitupapp;

public class TechnicianDetails {
    String name;
    String contact;
    String Zipcode;
    String type;
public TechnicianDetails(){

}
    public TechnicianDetails(String name, String contact, String zipcode, String type) {
        this.name = name;
        this.contact = contact;
        Zipcode = zipcode;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getZipcode() {
        return Zipcode;
    }

    public String getType() {
        return type;
    }
}
