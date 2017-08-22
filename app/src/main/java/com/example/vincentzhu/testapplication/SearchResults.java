package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SearchResults extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ArrayList<String> ingredient_list, recipe_list;
    private DatabaseReference mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        ingredient_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredientlist");

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

        // TODO: perform database search using this String
        mRoot = FirebaseDatabase.getInstance().getReference();

        mRoot.child("Ingredient_Recipes").addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void showData(DataSnapshot dataSnapshot) {

        HashSet<String> recipes = new HashSet<>(), ingredients = new HashSet<>(ingredient_list);

        for (String currentIngredient : ingredients) {

            try {
                HashMap<String, Object> current = (HashMap) dataSnapshot.child(currentIngredient).getValue();

                if(recipes.isEmpty())
                    recipes.addAll(current.keySet());
                else {
                    HashSet<String> currentRecipes = new HashSet<>(current.keySet());
                    recipes.retainAll(currentRecipes);
                }
            }
            catch(Exception e) {
                Log.d("Ingredient not found: ", currentIngredient);
            }
        }

        this.recipe_list = new ArrayList<>(recipes);
        updateList();

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

    // Create a list-item click-handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                // When the user clicks on a list item, it is removed from the list.
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    displayRecipe(recipe_list.get(position));
                }
            };

    private void updateList() {
        if(recipe_list.isEmpty())
            return;

        ArrayAdapter<String> adapter = null;
        ListView listView = null;

        for(String recipe : recipe_list) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipe_list);
            listView = (ListView) findViewById(R.id.lv_search_results);
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(itemClickListener);
    }

    /**
     * Simple method for displaying search results in a TextView for testing
     *

    private void displayResult(HashSet<String> result) {
        TextView tv_result = (TextView) findViewById(R.id.tv_result);
        for(String current : result)
            tv_result.append(current);
    }
     */
    public void displayRecipe(String recipe) {
        Intent intent = new Intent(SearchResults.this, RecipePage.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
