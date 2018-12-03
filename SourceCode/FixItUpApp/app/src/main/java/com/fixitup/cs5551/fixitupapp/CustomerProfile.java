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
import java.util.StringTokenizer;

public class CustomerProfile extends AppCompatActivity {

    TextView name;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
       name=(TextView)findViewById(R.id.name);
       btn=(Button)findViewById(R.id.book);
       Intent intent=getIntent();
       String techname = intent.getStringExtra("tech");
       StringTokenizer str = new StringTokenizer(techname);
       for(int i=1; str.hasMoreTokens(); i++){

          }

       name.setText(techname);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });
    }


}

