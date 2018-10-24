package com.fixitup.cs5551.fixitupapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHome extends AppCompatActivity {
   // Button btn;
    ListView list;
    DatabaseReference dbr;
    List<TechnicianDetails> technicianDetailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
       // btn = (Button)findViewById(R.id.display);
        list =(ListView)findViewById(R.id.listViewTechnicians);
        technicianDetailsList = new ArrayList<>();
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot technicianSnapshot: dataSnapshot.getChildren()){

                    TechnicianDetails td= technicianSnapshot.getValue(TechnicianDetails.class);
                    technicianDetailsList.add(td);
                }
               /* Log.e("Techinician", dataSnapshot.getChildren().toString()
                        );*/
                TechnicianList adapter = new TechnicianList(CustomerHome.this, technicianDetailsList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    }/*
    @Override
    protected void onStart(){
        super.onStart();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                technicianDetailsList.clear();
                for(DataSnapshot technicianSnapshot: dataSnapshot.getChildren()){
                    TechnicianDetails td= technicianSnapshot.getValue(TechnicianDetails.class);
                    technicianDetailsList.add(td);
                }
                TechnicianList adapter = new TechnicianList(CustomerHome.this, technicianDetailsList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
