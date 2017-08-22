package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedIngredients extends BaseActivity {


    //user-related
    private ArrayList<String> mUsernames = new ArrayList<>();
    private String userID;
    ExpandableListView expandableListView;

    ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
    ArrayList<String> parent = new ArrayList<String>();

    //database-related
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_saved_ingredients);
        super.onCreate(savedInstanceState);

        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);

        userID = firebaseAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child(userID).child("Ingredient");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(SavedIngredients.this, parent, listOfLists);
                expandableListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
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
        }
    }
}
