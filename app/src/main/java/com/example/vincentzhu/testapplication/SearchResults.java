package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private DatabaseReference mRoot;
    private TextView result;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is still logged in. If not, return to Login activity
        if (firebaseAuth.getCurrentUser() == null) {
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        // Get a support ActionBar corresponding to this toolbar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        query = intent.getStringExtra(SearchByName.EXTRA_SEARCH_QUERY);
        // TODO: perform database search using this String
        user = firebaseAuth.getCurrentUser();
        mRoot = FirebaseDatabase.getInstance().getReference();
        mRef = mRoot.child("Recipes");
        result = (TextView)findViewById(R.id.queryResult);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        ArrayList<String>array = new ArrayList<String>();
        String concat = "";
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            Log.i("Test result:", ds.toString());
            Log.i("Query", query);
            if(ds.getKey().contains(query))
            {
                array.add(ds.getKey());
                concat += ds.getKey() + "\n";
            }
        }

        result.setText(concat);
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
