package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipePage extends BaseActivity {

    private StorageReference storageRef;
    private String gs_url; // URL for recipe image in Firebase storage

    private ArrayList<String> info_labels;
    private ArrayList<ArrayList<String>> info_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recipe_page);
        super.onCreate(savedInstanceState);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String recipe = (String) getIntent().getSerializableExtra("recipe");

                // Get recipe name from database and display it in the TextView
                TextView tv_recipe_name = (TextView) findViewById(R.id.tv_recipe_name);
                tv_recipe_name.setText(dataSnapshot.child("Recipes")
                        .child(recipe).getKey());

                // Get the recipe image url and display it in the ImageView
                gs_url = dataSnapshot
                        .child("Recipes")
                        .child(recipe)
                        .child("Image").getValue(String.class);
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gs_url);
                ImageView iv_recipe_image = findViewById(R.id.iv_recipe_image);
                Glide.with(RecipePage.this)
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(iv_recipe_image);

                // Get recipe ingredients and instructions from the database and display them in
                // the ExpandableListView
                info_labels = new ArrayList<String>(Arrays.asList("Ingredients", "Instructions"));
                ArrayList<String> ingredients = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.child("Recipe_Ingredients")
                        .child(recipe).getChildren()) {
                    ingredients.add(ds.getValue(String.class));
                }
                ArrayList<String> instructions = new ArrayList<String>();
                instructions.add(dataSnapshot.child("Recipes")
                        .child(recipe)
                        .child("Instructions")
                        .getValue(String.class));
                info_contents = new ArrayList<ArrayList<String>>();
                info_contents.add(ingredients);
                info_contents.add(instructions);

                ExpandableListView elv_recipe_info =
                        (ExpandableListView) findViewById(R.id.elv_recipe_info);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(RecipePage.this,
                        info_labels, info_contents);
                elv_recipe_info.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRef.addValueEventListener(valueEventListener);
    }
}
