package com.fixitup.cs5551.fixitupapp;

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

        String techname = name.getText().toString().trim();
        String mobile = contact.getText().toString().trim();
        String zipcode = zip.getText().toString().trim();
        String type = specialization.getText().toString().trim();
        if (!TextUtils.isEmpty(techname)&& !TextUtils.isEmpty(mobile)&& !TextUtils.isEmpty(zipcode)&& !TextUtils.isEmpty(type)) {
            String id= dbr.push().getKey();
            TechnicianDetails td = new TechnicianDetails( techname, mobile,zipcode,type);
            dbr.child(id).setValue(td);
            Toast.makeText(this,"Technician is added",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this,"please enter details",Toast.LENGTH_LONG).show();
        }

    }
    }

