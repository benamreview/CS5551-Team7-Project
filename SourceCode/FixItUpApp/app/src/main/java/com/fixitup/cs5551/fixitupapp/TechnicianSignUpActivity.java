package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TechnicianSignUpActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mRegistration;
    String user_id;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get the information of the current user
                if (user != null){
                    //if not null, move to another activity, to be created later.
                    //Remember the current context!
                    Intent intent = new Intent(TechnicianSignUpActivity.this, TechnicianRegisterActivity.class);
                    intent.putExtra("user_id", user.getUid());
                    intent.putExtra("user_email", user.getEmail());
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        //Initialize variables from xml UI layout.
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mRegistration = (Button) findViewById(R.id.registration);
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        mRegistration.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(email.isEmpty()){
                    mEmail.setError("Email Address cannot be blank");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Enter a Valid Email");
                }
                else if(password.isEmpty()){
                    mPassword.setError("Password cannot be blank");
                }
                else if(password.length()<6 && password.length()>12){
                    mPassword.setError("Password should be of minimum 6 characters and maximum 12 characters");
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(TechnicianSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If user has already signed up, therefore the createUserWithEmailandPassword would fail.
                            if (!task.isSuccessful()) {
                                Toast.makeText(TechnicianSignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }
                            //If the user email cannot be found in the database, reference the database and add variables to it.
                            else {
                                user_id = mAuth.getCurrentUser().getUid(); //id assigned to Technician at moment of sign-up
                                //this database reference is pointing to the technicians
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(user_id);
                                current_user_db.setValue(true);
                                current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(user_id).child("email");
                                current_user_db.setValue(email);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}