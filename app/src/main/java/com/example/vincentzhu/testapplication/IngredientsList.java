package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * This activity displays the list of ingredients entered by the user.
 * The user is able to add and remove ingredients from the list or
 * search for recipes that use the ingredients in the list.
 */
public class IngredientsList extends AppCompatActivity {

    private ArrayList<String> ingredients_list; // list of ingredients entered by user

    // Create a list-item click-handling object as an anonymous class.
    // When the user clicks on a list item, it is removed from the list.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    ingredients_list.remove(position);
                    updateList();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Extract the string query entered by the user from the Home activity
        Intent intent = getIntent();
        String ingredient = intent.getStringExtra(Home.EXTRA_INGREDIENT);

        ingredients_list = new ArrayList<String>(); // initialize list of ingredients
        ingredients_list.add(ingredient); // Add ingredient from Home to list

        // Set the ListView's OnItemClickListener to handle clicking to remove list items
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClickListener);

        updateList();
    }

    /**
     * Called when the user taps the Add button.
     * Adds the ingredient entered by the user to the ingredients list.
     */
    public void addIngredient(View view) {
        EditText et_search_ingr = (EditText) findViewById(R.id.et_search_ingr);
        String query = et_search_ingr.getText().toString();
        if (!query.isEmpty()) { // Do not append if query is an empty string
            et_search_ingr.setText(""); // Clear text field when Add button is pressed
            ingredients_list.add(query);
            updateList();
        }
    }

    /**
     * Updates the ListView with entries from the ingredients list.
     * Should be called every time an entry is added to or removed from the list.
     * This method uses an ArrayAdapter to retrieve data from the ingredients ArrayList
     * and display each String entry as an item in the ListView.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ingredients_list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        // dynamic memory allocation issues? updateList() might be called frequently
    }

    /**
     * Called when the user taps the Clear List button.
     * Clears the ingredients list contained in the ListView.
     */
    public void clearList(View view) {
        ingredients_list.clear();
        updateList();
    }

    /**
     * Called when the user taps the Find Recipes button.
     */
    public void findRecipes(View view) {
        // TODO: Write findRecipes method
    }
}
