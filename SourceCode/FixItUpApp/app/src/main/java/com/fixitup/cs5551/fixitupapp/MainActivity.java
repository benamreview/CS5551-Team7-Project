package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton mTechnician, mCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTechnician = (ImageButton) findViewById(R.id.technician);
        mCustomer = (ImageButton) findViewById(R.id.customer);


        //Set behaviors for Customer and Technician buttons to redirect to its proper corresponding activity.
        mCustomer.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerLoginScreenActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mTechnician.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TechnicianLoginScreenActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}
