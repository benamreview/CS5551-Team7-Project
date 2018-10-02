package com.fixitup.cs5551.fixitupapp;

public class TechnicianDetails {
    String email;
    String name;
    String contact;
    String zipcode;
    String type;

    public TechnicianDetails(String Email, String Name, String Contact, String Zipcode, String Type) {
        email = Email;
        name = Name;
        contact = Contact;
        zipcode = Zipcode;
        type = Type;

    }
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getType() {
        return type;
    }
}
