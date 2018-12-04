package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Thread.sleep;

public class CustomerRatingActivity extends AppCompatActivity {

    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    String mRatingNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_rating);
        mRatingNum = "5";
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) findViewById(R.id.etFeedback);
        mSendFeedback = (Button) findViewById(R.id.btnSubmit);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                int mNum = (int) ratingBar.getRating();
                mRatingNum =  Integer.toString(mNum);
                switch (mNum) {
                    case 1:
                        mRatingScale.setText("Bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Fair");
                        break;
                    case 4:
                        mRatingScale.setText("Good!");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome!");
                        break;
                    default:
                        mRatingScale.setText("None");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRatingNum == null){
                    Toast.makeText(CustomerRatingActivity.this, "Please give a rating!", Toast.LENGTH_LONG).show();
                }
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(CustomerRatingActivity.this, "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = getIntent();
                    String technicianID = intent.getStringExtra("tID");
                    //Make changes to database
                    DatabaseReference technicianRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(technicianID).child("rating").child(mRatingNum);
                    if (technicianRef!=null){
                        technicianRef.setValue(true);
                    }


                    mFeedback.setText("");
                    mRatingBar.setRating(0);
                    Toast.makeText(CustomerRatingActivity.this, "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(CustomerRatingActivity.this, CustomerHome.class);
                    startActivity(intent);
                }
            }
        });

    }
}
