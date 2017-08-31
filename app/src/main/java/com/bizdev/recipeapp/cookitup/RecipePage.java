package com.bizdev.recipeapp.cookitup;

import android.content.Intent;
import android.os.Bundle;
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
    private ArrayList<String> info_labels;
    private ArrayList<ArrayList<String>> info_contents;

    private String recipe;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference dbRef;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_item_page);
        super.onCreate(savedInstanceState);

        checkFavorite(); // Check if the recipe is already a favorite of the user

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference();

        recipe = (String) getIntent().getSerializableExtra("SELECTED_ITEM");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get recipe name from database and display it in the TextView
                TextView tv_item_name = findViewById(R.id.tv_item_name);
                tv_item_name.setText(dataSnapshot.child("Recipes")
                        .child(recipe).getKey());

                // Get the recipe image url and display it in the ImageView
                if(dataSnapshot.child("Recipes").child(recipe).child("Image").getValue(String.class) != null){
                    gs_url = dataSnapshot
                            .child("Recipes")
                            .child(recipe)
                            .child("Image").getValue(String.class);}
                else
                {
                    gs_url = "gs://bizdevrecipeapp.appspot.com/Recipes/lRxFd3PSkGNfeUfZ3qOfpSRoaO12/restaurant-cutlery-circular-symbol-of-a-spoon-and-a-fork-in-a-circle_318-61086.jpg";
                }
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gs_url);
                ImageView iv_item_image = findViewById(R.id.iv_item_image);
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(iv_item_image);

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
        MenuItem fav_button = menu.findItem(R.id.action_favorite);
        // Change the Favorite button icon to a "filled heart" if the recipe is already a favorite
        if (isFavorite) {
            Toast.makeText(this, "Is already favorited", Toast.LENGTH_SHORT).show();
            fav_button.setIcon(R.drawable.ic_action_is_favorite);
        }
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
                startActivity(new Intent(this, MyIngredients.class));
                return true;
            case R.id.save_recipe:
                startActivity(new Intent(this, MyRecipes.class));
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
                startActivity(new Intent(this, Login.class));
                return true;
            case R.id.action_favorite:
                //favorites under the default tree
                setFavoriteRecipe(item);

                return true;
            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the user taps the Favorite button in the toolbar. The user's ID is written to
     * the database as a child of the subtree containing the recipe's information to indicate
     * that the user has favorited this recipe. Calls the favoriteExists() method to check if
     * the recipe is already a favorite of the user.
     *
     *
     * @param favorite_button
     */
    public void setFavoriteRecipe(final MenuItem favorite_button)
    {
        final DatabaseReference mCurrent = FirebaseDatabase.getInstance()
                .getReference()
                .child("Recipes")
                .child(recipe)
                .child("Favorited By");
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

    /**
     * Checks if the recipe is a favorite of the user.
     * @param dataSnapshot
     * @param userID
     * @return
     */
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

    /**
     * Called upon the start of this activity, when the toolbar options menu is created, to
     * determine the appearance of the Favorite (heart) icon. Checks if the recipe has been added to
     * the user's favorite recipes.
     *
     * @return
     */
    private void checkFavorite() {
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot favorited_by = dataSnapshot.child(recipe).child("Favorited By");
                if (favorited_by.exists()) {
                    for (DataSnapshot fav_user : favorited_by.getChildren()) {
                        if (userID.equals(fav_user.getKey())) {
                            isFavorite = true;
                            break;
                        }
                    }
                } else {
                    isFavorite = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
