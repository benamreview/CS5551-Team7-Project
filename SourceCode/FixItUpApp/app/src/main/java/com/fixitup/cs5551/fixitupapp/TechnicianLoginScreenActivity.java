package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class TechnicianLoginScreenActivity extends AppCompatActivity {
    Button signinBtn;
    Button signupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_login_screen);
        signinBtn = (Button) findViewById(R.id.signInBtn);
        signupBtn = (Button) findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance()!=null){
                    FirebaseAuth.getInstance().signOut();
                }
                Intent intent = new Intent(TechnicianLoginScreenActivity.this, TechnicianSignUpActivity.class);
                startActivity(intent);
                finish();
                return;
                }
        });
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TechnicianLoginScreenActivity.this, TechnicianLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
