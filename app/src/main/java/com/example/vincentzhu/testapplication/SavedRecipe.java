package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedRecipe extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    //private ListView mUserList;
    private ArrayList<String> mUsernames = new ArrayList<>();
    private String userID;
   // private TextView results;
    ExpandableListView expandableListView;

    ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
    ArrayList<String> parent = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipe);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        // Create the toolbar and set it as the app bar for the activity
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);


       // results = (TextView)findViewById(R.id.text_result);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Recipe");
      //  mUserList = (ListView)findViewById(R.id.user_list);
        userID = firebaseAuth.getCurrentUser().getUid();



        //final ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mUsernames);
        //mUserList.setAdapter(arrayAdapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(SavedRecipe.this, parent, listOfLists);
                expandableListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        //String display = "";


        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            Recipe recipe = new Recipe();

            recipe.setName(ds.getValue(Recipe.class).getName());
            recipe.setIngredients(ds.getValue(Recipe.class).getIngredients());
            recipe.setInstructions(ds.getValue(Recipe.class).getInstructions());

            ArrayList<String> child = new ArrayList<String>();
            parent.add(recipe.getName());
            child.add(recipe.getIngredients());
            child.add(recipe.getInstructions());
            listOfLists.add(child);

            //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            //mUserList.setAdapter(adapter);
        }
        //results.setText(display);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Search" item, show search dialog
                onSearchRequested();
                return true;
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
