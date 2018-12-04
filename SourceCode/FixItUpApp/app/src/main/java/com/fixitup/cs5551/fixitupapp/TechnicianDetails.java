package com.fixitup.cs5551.fixitupapp;

import android.os.Parcelable;

public class TechnicianDetails {
    String email;
    String name;
    String contact;
    String zipcode;
    String type;
    String fee;
      public TechnicianDetails(){
          email = "";
          name = "";
          contact = "";
          zipcode = "";
          type = "";
          fee="";
      }
    public TechnicianDetails(String Email, String Name, String Contact, String Zipcode, String Type, String Fee) {
        email = Email;
        name = Name;
        contact = Contact;
        zipcode = Zipcode;
        type = Type;
        fee = Fee;

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

    public String getFee(){
          return fee;
    }
}
