package com.fixitup.cs5551.fixitupapp;

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

        String custname = name.getText().toString().trim();
        String mobile = contact.getText().toString().trim();
        String zipcode = zip.getText().toString().trim();
        if (!TextUtils.isEmpty(custname)&& !TextUtils.isEmpty(mobile)&& !TextUtils.isEmpty(zipcode)) {
            String id= dbr.push().getKey();
            CustomerDetails cd = new CustomerDetails( custname, mobile, zipcode);
            dbr.child(id).setValue(cd);
            Toast.makeText(this,"Customer is added",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this,"please enter details",Toast.LENGTH_LONG).show();
        }

    }
}

