package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TechnicianRegisterActivity extends AppCompatActivity {
EditText name,contact, zip, specialization;
Button btn;
DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_register);
        btn = (Button) findViewById(R.id.detailButton);
        dbr= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
        name=(EditText)findViewById(R.id.editTextName);
        contact =(EditText)findViewById(R.id.editTextConatact);
        zip=(EditText)findViewById(R.id.editTextZip);
        specialization=(EditText)findViewById(R.id.editTextType);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             addTechnician();
            }
        });
    }
    public void addTechnician(){
        String tEmail = "";
        String tName = name.getText().toString().trim();
        String tMobile = contact.getText().toString().trim();
        String tZipcode = zip.getText().toString().trim();
        String tExpertise = specialization.getText().toString().trim();
        if (!TextUtils.isEmpty(tName)&& !TextUtils.isEmpty(tMobile)&& !TextUtils.isEmpty(tZipcode)&& !TextUtils.isEmpty(tExpertise)) {
            Intent intent = getIntent();
            String id= intent.getStringExtra("user_id");
            tEmail= intent.getStringExtra("user_email");
            TechnicianDetails td = new TechnicianDetails( tEmail,tName,tMobile,tZipcode,tExpertise);
            dbr.child(id).setValue(td);
            Toast.makeText(this,"Technician is added",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this,"Please enter details",Toast.LENGTH_LONG).show();
        }

    }
    }

