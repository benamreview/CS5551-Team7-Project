package com.fixitup.cs5551.fixitupapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerProfile extends AppCompatActivity {

    TextView Name;
    Button btn;
    private DrawerLayout dl;

    private ActionBarDrawerToggle abdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        dl = (DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_bar = (NavigationView)findViewById(R.id.nav_bar) ;
        nav_bar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.logout_cust)
                {
                    {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(CustomerProfile.this, MainActivity.class);
                        startActivity(intent);
                       // Toast.makeText(CustomerProfile.this, "logout", Toast.LENGTH_SHORT);
                    }
                }

                if(id == R.id.maps)
                {
                    Intent intent = new Intent(CustomerProfile.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                   // Toast.makeText(CustomerProfile.this, "map",Toast.LENGTH_SHORT);
                }

                if(id == R.id.settings_cust)
                {
                    Intent intent = new Intent(CustomerProfile.this, CustomerSettingsActivity.class);
                    startActivity(intent);
                    finish();
                   // Toast.makeText(CustomerProfile.this, "setting",Toast.LENGTH_SHORT);
                }


                return true;
            }
        });












       Name=(TextView)findViewById(R.id.name);
       btn=(Button)findViewById(R.id.book);
       Intent intent=getIntent();
        String s=intent.getStringExtra("tech");
        Name.setText(s);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerProfile.this);
                builder.setMessage("Would you like to request this Technician?")
                        .setPositiveButton("Yes, I Would!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(CustomerProfile.this, CustomerMapActivity.class);
                                intent.putExtra("requested", "true");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
