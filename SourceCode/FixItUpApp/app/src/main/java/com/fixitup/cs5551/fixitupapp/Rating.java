package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Rating extends AppCompatActivity{
    RatingBar mRating;
    Button mLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        //mAuth = FirebaseAuth.getInstance();
       // firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            //@Override
           // public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get the information of the current user
            //    if (user != null){
                    //if not null, move to another activity, to be created later.
                    //Remember the current context!
                //    Intent intent = new Intent(Rating.this, Rating.class);
                //    intent.putExtra("user_id", user.getUid());
                //    intent.putExtra("user_email", user.getEmail());
                //    startActivity(intent);
                    finish();
                //    return;
             //   }
           // }
        //};
        mRating = (RatingBar) findViewById(R.id.Ratingbar);
        mLogin = (Button) findViewById(R.id.submit);
        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(Rating.this,"stars" +(int)rating, Toast.LENGTH_LONG).show();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Rating.this,"stars:" + mRating.getRating(),Toast.LENGTH_SHORT).show();
            }
        });
       /* @Override
        protected void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(firebaseAuthListener);
        }

        @Override
        protected void onStop() {
            super.onStop();
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }*/

}
