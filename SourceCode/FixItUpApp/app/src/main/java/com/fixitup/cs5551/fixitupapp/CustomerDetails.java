package com.fixitup.cs5551.fixitupapp;

public class CustomerDetails {

    String name;
    String contact;
    String Zipcode;
    String email;
    public CustomerDetails(String email,String name, String contact, String zipcode) {
        this.name = name;
        this.contact = contact;
        Zipcode = zipcode;

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

}