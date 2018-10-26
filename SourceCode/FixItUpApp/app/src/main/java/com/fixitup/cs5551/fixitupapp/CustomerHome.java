package com.fixitup.cs5551.fixitupapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
    ListView lv;

    DatabaseReference dbr;
       TechnicianDetails td;
       ArrayList<String> list;
       ArrayAdapter<String> ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
        lv = (ListView)findViewById(R.id.listView);
        td = new TechnicianDetails();
        list = new ArrayList<>();
        ad = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.technician, list);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    td = ds.getValue(TechnicianDetails.class);
                    list.add(td.getName().toString()+"\n"+td.getEmail().toString()+"\n"+td.getContact().toString()+"\n "+td.getType().toString()+"\n"+td.getZipcode().toString());
                }
                lv.setAdapter(ad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    }
