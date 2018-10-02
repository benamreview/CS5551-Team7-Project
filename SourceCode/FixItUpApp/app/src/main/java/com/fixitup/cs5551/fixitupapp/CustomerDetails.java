package com.fixitup.cs5551.fixitupapp;

public class CustomerDetails {
    String email;
    String name;
    String contact;
    String zipcode;

    public CustomerDetails(String Email, String Name, String Contact, String Zipcode) {
        email = Email;
        name = Name;
        contact = Contact;
        zipcode = Zipcode;
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


}


