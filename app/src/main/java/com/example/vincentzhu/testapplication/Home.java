package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.sun.jersey.core.impl.provider.entity.Inflector;

public class Home extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);

        // Do not display Up button since this is the Home menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_menu_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the childNames of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // onClick handlers for buttons
        Button btn_add_ing = (Button) findViewById(R.id.btn_add_ing); //add personal ingredient button
        btn_add_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, PersonalIngredient.class));
            }
        });
        Button btn_add_recipe = (Button) findViewById(R.id.btn_add_recipe);
        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, PersonalRecipe.class));
            }
        });

    }

    /**
     * Handler for selecting items from the spinner drop-down menu.
     * Starts the activity for the corresponding item selected.
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // retrieve item using parent.getItemAtPosition() instead of getSelectedItem()
        String item = parent.getItemAtPosition(pos).toString();
        switch (item) {
            case "Recipe/Ingredient Name":
                spinner.setSelection(0);
                startActivity(new Intent(Home.this, SearchByName.class));
                break;
            case "Recipe Type":
                spinner.setSelection(0);
                startActivity(new Intent(Home.this, RecipeTypes.class));
                break;
            case "Recipe Cuisine":
                spinner.setSelection(0);
                startActivity(new Intent(Home.this, RecipeCuisines.class));
                break;
            case "Ingredient List":
                spinner.setSelection(0);
                startActivity(new Intent(Home.this, IngredientList.class));
                break;
            case "Ingredient Category":
                spinner.setSelection(0);
                startActivity(new Intent(Home.this, Ingredient_Categories.class));
                break;
            default:
                // Do nothing
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

    public void displaySavedRecipe(View view)
    {
        startActivity(new Intent(Home.this, SavedRecipe.class));
    }

    public void displaySavedIngredients(View view)
    {
        startActivity(new Intent(Home.this, SavedIngredients.class));
    }

    public void goToRecipePage(View view) {
        startActivity(new Intent(Home.this, RecipePage.class));
    }


}
