package com.bizdev.recipeapp.cookitup;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class Home extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference mRoot;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);

        getRecipeOfDay();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_recipe:
                        startActivity(new Intent(Home.this, PersonalRecipe.class));
                        return true;
                    case R.id.add_ing:
                        startActivity(new Intent(Home.this, PersonalIngredient.class));
                        return true;
                    case R.id.favorite_re:
                        startActivity(new Intent(Home.this, Favorites.class));
                        return true;
                    default:
                        return true;
                }
            }
        });

        String mainAccount = "devbizrecipe@gmail.com";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user.getEmail().equals(mainAccount)){
            //Profile activity here
            startActivity(new Intent(getApplicationContext(),Admin.class));
        }

        // Do not display Up button since this is the Home menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Spinner spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_menu_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the childNames of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    /**
     * Show the Exit Dialog when the user presses the Back button.
     */
    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    /**
     * Show the Exit Dialog to prompt the user to exit the app or cancel.
     */
    public void showExitDialog() {
        DialogFragment exitDialog = new ExitDialogFragment();
        exitDialog.show(getFragmentManager(), "ExitDialogFragment");
    }

    /**
     * Handler for selecting items from the spinner drop-down menu.
     * Starts the activity for the corresponding item selected.
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // retrieve item using parent.getItemAtPosition() instead of getSelectedItem()
        String item = parent.getItemAtPosition(pos).toString();
        Intent intent;

        switch (item) {
            case "Recipe Name":
                parent.setSelection(0);
                intent = new Intent(this, SearchByName.class);
                intent.putExtra("SEARCH_NAME", "Recipes");
                startActivity(intent);
                break;
            case "Recipe Type":
                parent.setSelection(0);
                intent = new Intent(this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Type_Recipes");
                startActivity(intent);
                break;
            case "Recipe Cuisine":
                parent.setSelection(0);
                intent = new Intent(this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Cuisine_Recipe");
                startActivity(intent);
                break;
            case "Recipe Catalog":
                parent.setSelection(0);
                displayAllRecipes();
                break;
            case "Ingredient List":
                parent.setSelection(0);
                startActivity(new Intent(this, IngredientList.class));
                break;
            case "Ingredient Name":
                parent.setSelection(0);
                intent = new Intent(this, SearchByName.class);
                intent.putExtra("SEARCH_NAME", "Ingredients");
                startActivity(intent);
                break;
            case "Ingredient Catalog":
                parent.setSelection(0);
                intent = new Intent(this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Type_Ingredients");
                startActivity(intent);
                break;
            default:
                // Do nothin
                break;
        }
    }

    /**
     * Handler for selecting nothing from the spinner drop-down menu.
     * Must be included when implementing AdapterView.OnItemSelectedListener interface.
     *
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                // User chose the "Log Out" item, log the user out and return to activity_registration activity
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

    /**
     * Gets a recipe from the database based on the current day of the year
     */
    private void getRecipeOfDay() {

        mRoot = FirebaseDatabase.getInstance().getReference();
        mRoot.child("Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> result = new ArrayList<>(((HashMap) dataSnapshot.getValue()).keySet());
                Calendar c = Calendar.getInstance();
                int day = (c.get(c.DAY_OF_YEAR))%result.size();
                String recipe = result.get(day);

                displayRecipeImage(recipe, dataSnapshot);
                displayRecipeOfDay(recipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     *  Displays the name of the recipe of the day and when clicking on
     *  either the name or the image the recipe page for the recipe of
     *  the day is displayed
     */
    private void displayRecipeOfDay(String r) {
        final TextView mTextView = findViewById(R.id.tv_daily_recipe_name);
        mTextView.setText(r);
        mImageView = findViewById(R.id.imageDisplay);

        final Intent intent = new Intent(Home.this, RecipePage.class);
        intent.putExtra("SELECTED_ITEM", r);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }

        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }

        });
    }

    /**
     * Gets the image of the recipe of the day from the database and sets the ImageView to it
     */
    private void displayRecipeImage(String recipe, DataSnapshot dataSnapshot) {
        // Get the recipe image url and display it in the ImageView
        String gs_url = dataSnapshot
                .child(recipe)
                .child("Image").getValue(String.class);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gs_url);
        ImageView iv_item_image = findViewById(R.id.imageDisplay);
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(iv_item_image);
    }

    /**
     * Displays all recipes currently in the database.
     */
    private void displayAllRecipes() {
        mRoot = FirebaseDatabase.getInstance().getReference();
        mRoot.child("Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> recipes = new ArrayList<>(((HashMap) dataSnapshot.getValue()).keySet());
                Collections.sort(recipes);
                Intent intent = new Intent(Home.this, SearchResults.class);
                intent.putExtra("RECIPE_RESULTS", recipes);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
