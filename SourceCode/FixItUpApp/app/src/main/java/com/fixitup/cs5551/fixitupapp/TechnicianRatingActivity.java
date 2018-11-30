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

public class TechnicianRatingActivity extends AppCompatActivity {

    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    String mRatingNum = "5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_rating);

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
                    Toast.makeText(TechnicianRatingActivity.this, "Please give a rating!", Toast.LENGTH_LONG).show();
                }
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(TechnicianRatingActivity.this, "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = getIntent();
                    String customerID = intent.getStringExtra("cID");
                    //Make changes to database
                    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID).child("rating").child(mRatingNum);
                    if (customerRef!=null){
                        customerRef.setValue(true);
                    }


                    mFeedback.setText("");
                    mRatingBar.setRating(0);
                    Toast.makeText(TechnicianRatingActivity.this, "Thank you for sharing your feedback!", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(TechnicianRatingActivity.this, TechnicianHome.class);
                    startActivity(intent);
                }
            }
        });

    }
}
