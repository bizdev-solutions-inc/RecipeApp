package com.example.vincentzhu.testapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Display ingredient details upon selection in the Ingredients Catalog, and Recipe Page
 */
public class IngredientPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference mRoot;
    private String ingredient;
    private String userID;

    private StorageReference storageRef;
    private String gs_url; // URL for recipe image in Firebase storage

    private ArrayList<String> info_labels;
    private ArrayList<ArrayList<String>> info_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_recipe_page);
        super.onCreate(savedInstanceState);

        //ingredient = new String();
        ingredient = (String)getIntent().getSerializableExtra("SELECTED_INGREDIENT");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredients").child(ingredient);

        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ingredientLookup(dataSnapshot);

                // Get the recipe image url and display it in the ImageView
                gs_url = dataSnapshot
                        .child("Image").getValue(String.class);
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gs_url);
                ImageView iv_item_image = findViewById(R.id.iv_item_image);
                Glide.with(IngredientPage.this)
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(iv_item_image);

                // Get recipe ingredients and instructions from the database and display them in
                // the ExpandableListView
                info_labels = new ArrayList<String>(Arrays.asList("Type", "Season", "Description", "History"));
                ArrayList<String> ingredientType = new ArrayList<String>();
                ArrayList<String> ingredientSeason = new ArrayList<String>();
                ArrayList<String> ingredientDescription = new ArrayList<String>();
                ArrayList<String> ingredientHistory = new ArrayList<String>();

                ingredientType.add(dataSnapshot.child("Type").getValue().toString());
                ingredientSeason.add(dataSnapshot.child("Season").getValue().toString());
                ingredientDescription.add(dataSnapshot.child("Description").getValue().toString());
                ingredientHistory.add(dataSnapshot.child("History").getValue().toString());

                info_contents = new ArrayList<ArrayList<String>>();
                info_contents.add(ingredientType);
                info_contents.add(ingredientSeason);
                info_contents.add(ingredientDescription);
                info_contents.add(ingredientHistory);

                ExpandableListView elv_item_info =
                        (ExpandableListView) findViewById(R.id.elv_item_info);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(IngredientPage.this,
                        info_labels, info_contents);
                elv_item_info.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void ingredientLookup(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            if(ds.getKey().equals(ingredient))
            {
                TextView tv_item_title = (TextView)findViewById(R.id.tv_item_title);
                tv_item_title.setText(ds.getKey().toString());
            }
        }
    }
}
