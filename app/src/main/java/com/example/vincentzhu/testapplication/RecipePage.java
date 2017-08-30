package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class RecipePage extends BaseActivity {

    private StorageReference storageRef;
    private String gs_url; // URL for recipe image in Firebase storage
    private String getActivity;
    private ArrayList<String> info_labels;
    private ArrayList<ArrayList<String>> info_contents;

    private String recipe;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_item_page);
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        getActivity = (String)getIntent().getSerializableExtra("GET_ACTIVITY");

        if(getActivity != null) {
            if (getActivity.equals("SavedRecipe")) {
                dbRef = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Recipes");
            }
            else if(getActivity.equals("SavedIngredients"))
            {
                dbRef = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Ingredients");
            }
            else if(getActivity.equals("Favorites"))
            {
                dbRef = FirebaseDatabase.getInstance().getReference();
            }
        }
        else
        {
            dbRef = FirebaseDatabase.getInstance().getReference();
        }

        recipe = (String) getIntent().getSerializableExtra("SELECTED_ITEM");
        Log.i("RECIPE", recipe);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get recipe name from database and display it in the TextView
                TextView tv_item_name = findViewById(R.id.tv_item_name);
                tv_item_name.setText(dataSnapshot.child("Recipes")
                        .child(recipe).getKey());

                // Get the recipe image url and display it in the ImageView
                gs_url = dataSnapshot
                        .child("Recipes")
                        .child(recipe)
                        .child("Image").getValue(String.class);
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gs_url);
                ImageView iv_item_image = findViewById(R.id.iv_item_image);
                Glide.with(RecipePage.this)
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(iv_item_image);

                // TODO: crashing at line 88

                // Get recipe ingredients and instructions from the database and display them in
                // the ExpandableListView
                info_labels = new ArrayList<>(Arrays.asList("Ingredients", "Instructions"));
                ArrayList<String> ingredients = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.child("Recipe_Ingredients")
                        .child(recipe).getChildren()) {
                    ingredients.add(ds.getValue(String.class));
                }
                ArrayList<String> instructions = new ArrayList<>();
                instructions.add(dataSnapshot.child("Recipes")
                        .child(recipe)
                        .child("Instructions")
                        .getValue(String.class));
                info_contents = new ArrayList<>();
                info_contents.add(ingredients);
                info_contents.add(instructions);

                ExpandableListView elv_item_info =
                        findViewById(R.id.elv_item_info);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(RecipePage.this,
                        info_labels, info_contents);
                elv_item_info.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRef.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_home:
                // User chose the "Home" item, show the Home activity
                startActivity(new Intent(this, Home.class));
                return true;
            case R.id.save_ing:
                startActivity(new Intent(this, SavedIngredients.class));
                return true;
            case R.id.save_recipe:
                startActivity(new Intent(this, SavedRecipe.class));
                return true;
            case R.id.recipe_favorites:
                startActivity(new Intent(this, Favorites.class));
                return true;
            case R.id.action_about_us:
                // User chose the "About Us" item, show the About Us activity
                startActivity(new Intent(this, AboutUs.class));
                return true;
            case R.id.action_logout:
                // User chose the "Log Out" item, log the user out and return to Registration activity
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.action_favorite:
                final DatabaseReference mFavorite = FirebaseDatabase.getInstance().getReference();

                if(getActivity!=null)
                {
                    if(getActivity.equals("SavedRecipe"))
                    {
                        final DatabaseReference mCurrent = mFavorite.child(userID)
                                .child("Added Recipes")
                                .child("Recipes")
                                .child(recipe)
                                .child("Favorited By");
                        setFavoriteRecipe(mCurrent, item);
                    }
                }
                else
                {
                    final DatabaseReference mCurrent = mFavorite.child("Recipes").child(recipe).child("Favorited By");
                    setFavoriteRecipe(mCurrent, item);
                }
                return true;
            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFavoriteRecipe(final DatabaseReference mCurrent, final MenuItem favorite_button)
    {
        if(mCurrent != null) { // "Favorited By" attribute exists
            mCurrent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!favoriteExists(dataSnapshot, userID)) {
                        mCurrent.child(userID).setValue(userID);
                        favorite_button.setIcon(R.drawable.ic_action_is_favorite);
                        Toast.makeText(RecipePage.this, "Added to Favorites",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        mCurrent.child(userID).removeValue();
                        favorite_button.setIcon((R.drawable.ic_action_is_not_favorite));
                        Toast.makeText(RecipePage.this, "Removed from Favorites",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else // "Favorited By" attribute does not exist
        {
            mCurrent.child(userID).setValue(userID);
        }

    }

    public boolean favoriteExists(DataSnapshot dataSnapshot, String userID)
    {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            if(ds.getKey().equals(userID))
            {
                return true;
            }
        }
        return false;
    }
}
