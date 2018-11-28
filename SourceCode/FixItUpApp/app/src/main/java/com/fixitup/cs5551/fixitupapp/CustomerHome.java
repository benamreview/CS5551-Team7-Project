package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHome extends AppCompatActivity {
    //ListView lv;
    private Button mMapBtn;
    private Button search;
    private EditText type;
    String ft;
    ListView lv;
    String st;
    DatabaseReference dbr;
    TechnicianDetails td;
    ArrayList<String> list;
    ArrayAdapter<String> ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        search=(Button)findViewById(R.id.btn);
        type=(EditText)findViewById(R.id.search);
        ft=type.getText().toString().trim();
        //
        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
        lv = (ListView)findViewById(R.id.listView);
        td = new TechnicianDetails();
        list = new ArrayList<>();
        ad = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.technician, list);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query q = dbr.orderByChild("type").equalTo(type.getText().toString().trim());
                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //To-be implemented: have a spinner and s
                        list.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            //String s = dataSnapshot.child("type").getValue().toString();

                            td = ds.getValue(TechnicianDetails.class);
                            //  if(s.equalsIgnoreCase(st)) {
                            // boolean b= st.equalsIgnoreCase(td.getType().toString());
                            //if(b==true){
                            list.add(td.getName().toString() + "\n" + td.getEmail().toString() + "\n" + td.getContact().toString() + "\n " + td.getType().toString() + "\n" + td.getZipcode().toString());
                            //}
                        }
                        lv.setAdapter(ad);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object o = parent.getItemAtPosition(position);
                                Intent i = new Intent(CustomerHome.this, CustomerProfile.class);
                                startActivity(i);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        mMapBtn = (Button) findViewById(R.id.mapBtn);
        mMapBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHome.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


    }
