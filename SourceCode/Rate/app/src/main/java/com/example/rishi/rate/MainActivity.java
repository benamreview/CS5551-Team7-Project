package com.example.rishi.rate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    RatingBar mRating;
    Button mButton;
    private DatabaseReference mDatabase,mDemo;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRating = (RatingBar) findViewById(R.id.ratingBar);
        mButton = (Button) findViewById(R.id.button);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDemo = mDatabase.child("rating");


        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(MainActivity.this,"stars:" + rating,Toast.LENGTH_SHORT).show();
            }
        });
       mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this,"stars:"+ mRating.getRating(),Toast.LENGTH_SHORT).show();
                float value = Float.valueOf(mRating.getRating());
                mDemo.push().setValue(value);
            }
        });

    }
}
