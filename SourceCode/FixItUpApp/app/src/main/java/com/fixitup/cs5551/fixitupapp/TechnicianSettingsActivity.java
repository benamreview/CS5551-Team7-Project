package com.fixitup.cs5551.fixitupapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TechnicianSettingsActivity extends AppCompatActivity {
    EditText name,contact, zip, specialization, fee;
    Button btn, backBtn;
    DatabaseReference dbr;
    DatabaseReference currentTechnicianRef;
    String userID;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_settings);

        btn = (Button) findViewById(R.id.detailButton);
        backBtn = (Button) findViewById(R.id.back);

        dbr= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentTechnicianRef = dbr.child(userID);

        name=(EditText)findViewById(R.id.editTextName);
        contact =(EditText)findViewById(R.id.editTextConatact);
        zip=(EditText)findViewById(R.id.editTextZip);
        specialization=(EditText)findViewById(R.id.editTextType);
        fee = (EditText)findViewById(R.id.editTextCharge);

        currentTechnicianRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //To-be implemented: have a spinner and s
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.getKey().equals("name")){
                        name.setText(ds.getValue().toString());
                    }
                    else if (ds.getKey().equals("contact")){
                        contact.setText(ds.getValue().toString());
                    }
                    else if (ds.getKey().equals("zipcode")){
                        zip.setText(ds.getValue().toString());
                    }
                    else if (ds.getKey().equals("type")){
                        specialization.setText(ds.getValue().toString());
                    }
                    else if (ds.getKey().equals("fee")){
                        fee.setText(ds.getValue().toString());
                    }
                }
            }
            //For tomorrow: add logic for confirm and back button
            //Add fee and calculation (if possible)

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTechnician();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TechnicianSettingsActivity.this);
                builder.setMessage(R.string.warning_msg3)
                        .setPositiveButton("Yes, I Understand!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(TechnicianSettingsActivity.this, TechnicianHome.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();

            }
        });
    }
    public void addTechnician(){
        String tEmail = "";
        String tName = name.getText().toString().trim();
        String tMobile = contact.getText().toString().trim();
        String tZipcode = zip.getText().toString().trim();
        String tExpertise = specialization.getText().toString().trim();
        String tFee = fee.getText().toString().trim();
        if(TextUtils.isEmpty(tName)&& TextUtils.isEmpty(tMobile)&& TextUtils.isEmpty(tZipcode)&& TextUtils.isEmpty(tExpertise)) {
            Toast.makeText(this,"Please enter details",Toast.LENGTH_LONG).show();
        }
        else if(tName.isEmpty()){
            name.setError("Name should not be blank");
        }
        else if(tName.length()>30){
            name.setError("Name should not exceed 30 characters");
        }
        else if(tMobile.isEmpty()){
            contact.setError("Mobile number should not be blank");
        }
        else if(tMobile.length()!=10){
            contact.setError("Enter a valid Mobile number");
        }
        else if(tZipcode.isEmpty()){
            zip.setError("Zipcode cannot be blank");
        }
        else if(tZipcode.length()!=5){
            zip.setError("Enter a valid Zipcode ");
        }
        else if(tExpertise.isEmpty()){
            specialization.setError("specialization cannot be blank");
        }
        // if (!TextUtils.isEmpty(tName)&& !TextUtils.isEmpty(tMobile)&& !TextUtils.isEmpty(tZipcode)&& !TextUtils.isEmpty(tExpertise)) {
        else{
            final TechnicianDetails td = new TechnicianDetails( userEmail, tName, tMobile, tZipcode, tExpertise, tFee);
            AlertDialog.Builder builder = new AlertDialog.Builder(TechnicianSettingsActivity.this);
            builder.setMessage(R.string.warning_msg2)
                    .setPositiveButton("Yes, I Understand!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dbr.child(userID).setValue(td);
                            Toast.makeText(TechnicianSettingsActivity.this,"Information Updated Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.show();

        }
    }
}

