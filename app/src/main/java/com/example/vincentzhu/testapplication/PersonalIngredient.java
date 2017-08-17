package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalIngredient extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    //private DatabaseReference mRef;
    private EditText mIngName;
    private EditText mIngDescription;
    private EditText mIngType;
    private EditText mIngHistory;
    private EditText mIngSeason;

    private DatabaseReference mName;
    private DatabaseReference mDescription;
    private DatabaseReference mType;
    private DatabaseReference mHistory;
    private DatabaseReference mSeason;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_ingredient);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        // Create the toolbar and set it as the app bar for the activity
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar and enable Up button
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        mFirebaseBtn = (Button) findViewById(R.id.firebase_ing_btn);
        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(userID).child("Ingredient");

        mIngName = (EditText) findViewById(R.id.ing_name);
        mIngDescription = (EditText) findViewById(R.id.ing_description);
        mIngType = (EditText) findViewById(R.id.ing_type);
        mIngHistory = (EditText) findViewById(R.id.ing_history);
        mIngSeason = (EditText) findViewById(R.id.ing_season);

        mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mIngName.getText().toString().trim();
                String description = mIngDescription.getText().toString().trim();
                String type = mIngType.getText().toString().trim();
                String history = mIngHistory.getText().toString().trim();
                String season = mIngSeason.getText().toString().trim();

                mName = mDatabase.child(name);
                mDescription = mName.child("description");
                mType = mName.child("type");
                mHistory = mName.child("history");
                mSeason = mName.child("season");
                mDescription.setValue(description);
                mType.setValue(type);
                mHistory.setValue(history);
                mSeason.setValue(season);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                // User chose the "Home" item, show the Home activity
                finish();
                startActivity(new Intent(this, Home.class));
                return true;
            case R.id.action_about_us:
                // User chose the "About Us" item, show the About Us activity
                finish();
                startActivity(new Intent(this, AboutUs.class));
                return true;
            case R.id.action_logout:
                // User chose the "Log Out" item, log the user out and return to login activity
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}

