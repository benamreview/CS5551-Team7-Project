package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegisterActivity extends AppCompatActivity {

    EditText name,contact, zip;
    Button btn;
    DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        btn = (Button) findViewById(R.id.detailButton);
        dbr= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        name=(EditText)findViewById(R.id.editTextName);
        contact =(EditText)findViewById(R.id.editTextConatact);
        zip=(EditText)findViewById(R.id.editTextZip);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer();
            }
        });
    }
    public void addCustomer(){

        String cEmail = "";
        String cName = name.getText().toString().trim();
        String cMobile = contact.getText().toString().trim();
        String cZipcode = zip.getText().toString().trim();
        if (!TextUtils.isEmpty(cName)&& !TextUtils.isEmpty(cMobile)&& !TextUtils.isEmpty(cZipcode)) {
            Intent intent = getIntent();
            String id= intent.getStringExtra("user_id");
            cEmail= intent.getStringExtra("user_email");
            CustomerDetails cd = new CustomerDetails(cEmail, cName, cMobile, cZipcode);
            dbr.child(id).setValue(cd);
            Toast.makeText(this,"Customer is added",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this,"Please enter details",Toast.LENGTH_LONG).show();
        }

    }
}

