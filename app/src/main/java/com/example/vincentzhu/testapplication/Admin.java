package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRoot;
    private DatabaseReference mIngredients;
    private DatabaseReference mRecipe_Ingredients;
    private DatabaseReference mIngredient_Recipes;
    private DatabaseReference mCuisine_Recipe;
    private DatabaseReference mRecipes;
    private DatabaseReference mType_Recipes;
    private DatabaseReference mType_Ingredients;

    Button addRecipe;
    Button addIngredient;

    EditText recipe_name;
    EditText recipe_instruction;
    EditText recipe_ingredients;
    EditText ingredient_name;
    EditText ingredient_description;
    EditText ingredient_history;

    Spinner spinner_recipe_type;
    Spinner spinner_cuisine;
    Spinner spinner_ing_type;
    Spinner spinner_ing_season;
    ArrayAdapter<CharSequence>adapter_recipe_type;
    ArrayAdapter<CharSequence>adapter_cuisine;
    ArrayAdapter<CharSequence>adapter_ing_cuisine;
    ArrayAdapter<CharSequence>adapter_ing_season;

    private String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        addRecipe = (Button)findViewById(R.id.save_recipe);
        addIngredient = (Button)findViewById(R.id.save_ingredient);
        recipe_name = (EditText)findViewById(R.id.recipeName);
        recipe_instruction = (EditText)findViewById(R.id.recipeInstructions);
        recipe_ingredients = (EditText)findViewById(R.id.recipeIngredients);
        ingredient_name = (EditText)findViewById(R.id.ingredientName);
        ingredient_description = (EditText)findViewById(R.id.ingredientDescription);
        ingredient_history = (EditText)findViewById(R.id.ingredientHistory);

        spinner_recipe_type = (Spinner)findViewById(R.id.spinner_type);
        spinner_cuisine = (Spinner)findViewById(R.id.spinner_cuisine);
        spinner_ing_type = (Spinner)findViewById(R.id.spinner_ing_type);
        spinner_ing_season = (Spinner)findViewById(R.id.spinner_ing_season);
        adapter_recipe_type = ArrayAdapter.createFromResource(this, R.array.recipe_types,android.R.layout.simple_spinner_item);
        adapter_cuisine = ArrayAdapter.createFromResource(this, R.array.recipe_cuisines,android.R.layout.simple_spinner_item);
        adapter_ing_cuisine = ArrayAdapter.createFromResource(this, R.array.ingredient_types,android.R.layout.simple_spinner_item);
        adapter_ing_season = ArrayAdapter.createFromResource(this, R.array.ingredient_seasons,android.R.layout.simple_spinner_item);
        adapter_recipe_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_cuisine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_ing_cuisine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_ing_season.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_recipe_type.setAdapter(adapter_recipe_type);
        spinner_cuisine.setAdapter(adapter_cuisine);
        spinner_ing_type.setAdapter(adapter_ing_cuisine);
        spinner_ing_season.setAdapter(adapter_ing_season);

        userID = firebaseAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child(userID);

        mIngredients = mRoot.child("Ingredients");
        mRecipe_Ingredients = mRoot.child("Recipe_Ingredients");
        mIngredient_Recipes = mRoot.child("Ingredient_Recipes");
        mCuisine_Recipe = mRoot.child("Cuisine_Recipe");
        mRecipes = mRoot.child("Recipes");
        mType_Recipes = mRoot.child("Type_Recipes");
        mType_Ingredients = mRoot.child("Type_Ingredients");

        spinner_recipe_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), adapterView.getItemIdAtPosition(i) + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeName = recipe_name.getText().toString().trim();
                String recipeInstruction = recipe_instruction.getText().toString().trim();
                String recipeIngredients = recipe_ingredients.getText().toString() + " "; //need to update later
                ArrayList<String> parse = new ArrayList<String>();
                parseString(recipeIngredients, parse);

                //Method 3
                mCuisine_Recipe.child(spinner_cuisine.getSelectedItem().toString()).child(recipeName).setValue(recipeName);
                //Method 4
                mType_Recipes.child(spinner_recipe_type.getSelectedItem().toString()).child(recipeName).setValue(recipeName);
                //Method 5
                mRecipes.child(recipeName).child("Type").setValue(spinner_recipe_type.getSelectedItem().toString());
                mRecipes.child(recipeName).child("Cuisine").setValue(spinner_cuisine.getSelectedItem().toString());
                mRecipes.child(recipeName).child("Instructions").setValue(recipeInstruction);

                //Method 6 & 7
                for(String word : parse)
                {
                    mRecipe_Ingredients.child(recipeName).child(word).setValue(word);
                    mIngredient_Recipes.child(word).child(recipeName).setValue(recipeName);
                }
            }
        });

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingredientName = ingredient_name.getText().toString().trim();
                String ingredientDescription = ingredient_description.getText().toString().trim();
                String ingredientHistory = ingredient_history.getText().toString().trim();

                mIngredients = mRoot.child("Ingredients");
                //Method 1
                mIngredients.child(ingredientName).child("Description").setValue(ingredientDescription);
                mIngredients.child(ingredientName).child("Type").setValue(spinner_ing_type.getSelectedItem().toString());
                mIngredients.child(ingredientName).child("History").setValue(ingredientHistory);
                mIngredients.child(ingredientName).child("Season").setValue(spinner_ing_season.getSelectedItem().toString());

                //Method 2
                mType_Ingredients = mRoot.child("Type_Ingredients");
                mType_Ingredients.child(spinner_ing_type.getSelectedItem().toString()).child(ingredientName).setValue(ingredientName);

            }
        });
    }


    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_search:
//                // User chose the "Search" item, show search dialog
//                onSearchRequested();
//                return true;
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

    public void parseString(String line, ArrayList<String> parse)
    {
        int startIndex = 0;
        int endIndex = 0;
        boolean found = false;

        for(int i=0; i<line.length(); i++)
        {
            if(line.charAt(i)!= ' ' && found == false)
            {
                startIndex = i;
                found = true;
            }
            else if(line.charAt(i) == ' ' && found == true)
            {
                endIndex = i;
                parse.add(line.substring(startIndex, endIndex));
                found = false;
            }
        }
    }
}
