package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Display ingredient details upon selection in the Ingredients Catalog, and Recipe Page
 */
public class IngredientPage extends BaseActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference mRoot;
    private String ingredient;
    private String userID;
    private String getActivity;
    private boolean isFavorite;

    private StorageReference storageRef;
    private String gs_url; // URL for recipe image in Firebase storage

    private ArrayList<String> info_labels;
    private ArrayList<ArrayList<String>> info_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_item_page);
        super.onCreate(savedInstanceState);

        checkFavorite();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        getActivity = (String) getIntent().getSerializableExtra("GET_ACTIVITY");

        ingredient = (String) getIntent().getSerializableExtra("SELECTED_INGREDIENT");

        // Hide the text hint "(Tap an ingredient for more info)" from the layout
        TextView tv_hint = findViewById(R.id.tv_hint);
        tv_hint.setVisibility(View.GONE);

        if (getActivity != null) {
            if (getActivity.equals("SavedIngredients")) {
                mRoot = FirebaseDatabase.getInstance().getReference()
                        .child(userID).child("Added Ingredients")
                        .child("Ingredients").child(ingredient);
            }
        } else {
            mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredients").child(ingredient);
        }


        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setName(dataSnapshot);

                // Get the ingredient image url and display it in the ImageView
                if(dataSnapshot.child("Image").getValue(String.class) != null)
                {
                    gs_url = dataSnapshot
                            .child("Image").getValue(String.class);
                }
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

                // Get ingredient type, season, description, and history and display them
                // in the ExpandableListView
                info_labels = new ArrayList<>
                        (Arrays.asList("Type", "Season", "Description", "History"));
                ArrayList<String> ingredientType = new ArrayList<>();
                ArrayList<String> ingredientSeason = new ArrayList<>();
                ArrayList<String> ingredientDescription = new ArrayList<>();
                ArrayList<String> ingredientHistory = new ArrayList<>();

                ingredientType.add(dataSnapshot.child("Type").getValue(String.class));
                ingredientSeason.add(dataSnapshot.child("Season").getValue(String.class));
                ingredientDescription.add(dataSnapshot.child("Description").getValue(String.class));
                ingredientHistory.add(dataSnapshot.child("History").getValue(String.class));

                info_contents = new ArrayList<>();
                info_contents.add(ingredientType);
                info_contents.add(ingredientSeason);
                info_contents.add(ingredientDescription);
                info_contents.add(ingredientHistory);

                ExpandableListView elv_item_info = findViewById(R.id.elv_item_info);
                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter
                        (IngredientPage.this, info_labels, info_contents);
                elv_item_info.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Query the database to get the name of the ingredient and set it as the text in the TextView.
     * The ingredient name is capitalized using the WordUtils library function.
     *
     * @param dataSnapshot The DataSnapshot of the database tree node to query
     */
    public void setName(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(ingredient)) {
                TextView tv_item_name = findViewById(R.id.tv_item_name);
                tv_item_name.setText(WordUtils.capitalize(ds.getKey()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        MenuItem fav_button = menu.findItem(R.id.action_favorite);
        // Change the Favorite button icon to a "filled heart"
        // if the ingredient is already a favorite
        if (isFavorite) {
            fav_button.setIcon(R.drawable.ic_action_is_favorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userID = user.getUid();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_home:
                // User chose the "Home" item, show the Home activity
                startActivity(new Intent(this, Home.class));
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
                // User chose the "Log Out" item,
                // log the user out and return to Registration activity
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.action_favorite:
                final DatabaseReference mFavorite = FirebaseDatabase.getInstance().getReference();
                if (getActivity != null) {
                    if (getActivity.equals("SavedIngredients")) {
                        final DatabaseReference mCurrent = mFavorite.child(userID)
                                .child("Added Ingredients")
                                .child("Ingredients")
                                .child(ingredient)
                                .child("Favorited By");
                        setFavoriteIngredient(mCurrent, item);
                    }
                } else {
                    final DatabaseReference mCurrent = mFavorite.child("Ingredients")
                            .child(ingredient)
                            .child("Favorited By");
                    setFavoriteIngredient(mCurrent, item);
                }
                return true;
            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the user taps the Favorite button in the toolbar. The user's ID is written to
     * the database as a child of the subtree containing the ingredient's information to indicate
     * that the user has favorited this ingredient. Calls the favoriteExists() method to check if
     * the ingredient is already a favorite of the user.
     * @param mCurrent
     * @param favorite_button
     */
    public void setFavoriteIngredient(final DatabaseReference mCurrent,
                                      final MenuItem favorite_button) {
        if (mCurrent != null) { // "Favorited By" attribute exists
            mCurrent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!favoriteExists(dataSnapshot, userID)) {
                        mCurrent.child(userID).setValue(userID);
                        favorite_button.setIcon(R.drawable.ic_action_is_favorite);
                        Toast.makeText(IngredientPage.this, "Added to Favorites",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        mCurrent.child(userID).removeValue();
                        favorite_button.setIcon((R.drawable.ic_action_is_not_favorite));
                        Toast.makeText(IngredientPage.this, "Removed from Favorites",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else // "Favorited By" attribute does not exist
        {
            mCurrent.child(userID).setValue(userID);
        }
    }

    /**
     * Checks if the ingredient is a favorite of the user.
     * @param dataSnapshot
     * @param userID
     * @return
     */
    public boolean favoriteExists(DataSnapshot dataSnapshot, String userID) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(userID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called upon the start of this activity, when the toolbar options menu is created, to
     * determine the appearance of the Favorite (heart) icon. Checks if the ingredient has been
     * added to the user's favorite ingredients.
     */
    private void checkFavorite() {
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference()
                .child("Ingredients");
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot favorited_by = dataSnapshot.child(ingredient).child("Favorited By");
                if (dataSnapshot.child(ingredient).child("Favorited By").exists()) {
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
