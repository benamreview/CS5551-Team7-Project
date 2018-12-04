package com.fixitup.cs5551.fixitupapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnicianHome extends AppCompatActivity {
    ListView lv;
    private Button mMapBtn, mLogout, mSettings;

    private ImageView mProfileImage;

    private String mProfileURL;

    private Uri resultURI;

    private String userID;

    private DrawerLayout dl;

    private ActionBarDrawerToggle abdt;

    private TextView mDate;

    private DatabaseReference mTechnicianDatabase;
    DatabaseReference dbr;
    TechnicianDetails td;
    ArrayList<String> list;
    ArrayAdapter<String> ad;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_home);


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

                if(id == R.id.logout_tech)
                {
                    {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(TechnicianHome.this, TechnicianSettingsActivity.class);
                        startActivity(intent);
                         Toast.makeText(TechnicianHome.this, "logout", Toast.LENGTH_SHORT);
                    }
                }

                if(id == R.id.maps)
                {
                    Intent intent = new Intent(TechnicianHome.this, TechnicianMapActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(TechnicianHome.this, "map",Toast.LENGTH_SHORT);
                }

                if(id == R.id.settings_tech)
                {
                    Intent intent = new Intent(TechnicianHome.this, TechnicianSettingsActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(TechnicianHome.this, "setting",Toast.LENGTH_SHORT);
                }


                return true;
            }
        });
        mDate = (TextView) findViewById(R.id.date);
        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                mDate.setText(Calendar.getInstance().getTime().toString());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();


        mProfileImage = (ImageView) findViewById(R.id.profileImg);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mTechnicianDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(userID);

        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians");
        lv = (ListView)findViewById(R.id.listView);
        td = new TechnicianDetails();
        list = new ArrayList<>();
        ad = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.technician, list);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //To-be implemented: have a spinner and s
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    td = ds.getValue(TechnicianDetails.class);
                    list.add(td.getName().toString()+"\n"+td.getEmail().toString()+"\n"+td.getContact().toString()+"\n "+td.getType().toString()+"\n"+td.getZipcode().toString());
                }
                lv.setAdapter(ad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Map Btn
        //To-be-implemented: Display map on this screen.
       // mMapBtn = (Button) findViewById(R.id.mapBtn);

        //Click a picture
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); //restricts selection
                startActivityForResult(intent, 1);
            }
        });
        getUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //See if it returns the same code from the startactivityforresult above
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageURI = data.getData();
            resultURI = imageURI;
            mProfileImage.setImageURI(resultURI);
            applyChanges();
        }
    }

    //Apply changes for profile images on Firebase Storage
    //Note: need to enable storage first for permission issues.
    private void applyChanges(){

        if (resultURI != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            //Add uri to bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //compress images
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            //Upload task will save the image to Firebase Database
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageURL", uri.toString());
                            mTechnicianDatabase.updateChildren(newImage);
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        else {
            finish();
        }
    }
    private void getUserInfo(){
        mTechnicianDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageURL") != null){
                        mProfileURL = map.get("profileImageURL").toString();
                        Glide.with(getApplication()).load(mProfileURL).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}

