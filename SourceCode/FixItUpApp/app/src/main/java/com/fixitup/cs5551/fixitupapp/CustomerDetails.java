package com.fixitup.cs5551.fixitupapp;

public class CustomerDetails {

    String name;
    String contact;
    String zipcode;
    String email;
    public CustomerDetails(String Email,String Name, String Contact, String Zipcode) {
        name = Name;
        email = Email;
        contact = Contact;
        zipcode = Zipcode;

    }
    public String getEmail(){ return email;}

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getZipcode() {
        return zipcode;
    }

}