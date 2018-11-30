package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerProfile extends AppCompatActivity {

    TextView Name,Email,Contact,Fee;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
       Name=(TextView)findViewById(R.id.name);
       Email=(TextView)findViewById(R.id.email);
        Contact=(TextView)findViewById(R.id.contact);
        Fee=(TextView)findViewById(R.id.fee);
       btn=(Button)findViewById(R.id.book);
       Intent intent=getIntent();
       String s1 = intent.getStringExtra("name");
       String s2 = intent.getStringExtra("email");
        String s3 = intent.getStringExtra("contact");
        String s4 = intent.getStringExtra("fee");
        String s5 = intent.getStringExtra("id");
       Name.setText(s1);
       Email.setText(s2);
       Contact.setText(s3);
       Fee.setText(s4);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });



    }


}

