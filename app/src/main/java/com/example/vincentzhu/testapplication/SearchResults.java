package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//I am Ashot!

public class SearchResults extends BaseActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private TextView result;
    private String query;
    private ArrayList<String> ingredient_list;
    private DatabaseReference mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        // Get the ingredient list entered by user from IngredientList activity
        ingredient_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredientlist");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        query = intent.getStringExtra(SearchByName.EXTRA_SEARCH_QUERY);
        // TODO: perform database search using this String from SearchByName activity

        user = firebaseAuth.getCurrentUser();
        mRoot = FirebaseDatabase.getInstance().getReference();
        mRef = mRoot.child("Recipes");

        mRoot.child("Ingredient_Recipes").addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void showData(DataSnapshot dataSnapshot) {

        HashSet<String> recipes = new HashSet<>();
        HashSet<String> ingredients = new HashSet<>(ingredient_list);

        for (String currentIngredient : ingredients) {

            HashMap<String, Object> current =
                    (HashMap) dataSnapshot.child(currentIngredient).getValue();

            if(recipes.isEmpty())
                recipes.addAll(current.keySet());
            else {
                HashSet<String> currentRecipes = new HashSet<>(current.keySet());
                recipes.retainAll(currentRecipes);
            }
        }

        displayResult(recipes);
    }

    /**
     * Method for displaying the search results as items in a ListView.
     * @param result
     */
    private void displayResult(HashSet<String> result) {
        ArrayList<String> results_list = new ArrayList<String>(result);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResults.this,
                android.R.layout.simple_list_item_1, results_list);
        ListView lv_search_results = (ListView) findViewById(R.id.lv_search_results);
        lv_search_results.setAdapter(adapter);
    }

    /**
     * Displays the RecipePage for the recipe selected by the user.
     */
    private void displayRecipePage() {
        // TODO: write displayRecipePage method
    }

}
