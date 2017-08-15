package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This activity displays the list of ingredients entered by the user.
 * The user is able to add and remove ingredients from the list or
 * search for recipes that use the ingredients in the list.
 */
public class IngredientsList extends AppCompatActivity {

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

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(Home.EXTRA_INGREDIENT);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);

    }

    /**
     * Called when the user taps the Add button.
     * Adds the ingredient entered by the user to the ingredients list.
     */
    public void addIngredient(View view) {
        EditText et_search_ingr = (EditText) findViewById(R.id.et_search_ingr);
        String query = et_search_ingr.getText().toString();
        TextView textView = (TextView) findViewById(R.id.textView);
        if (!query.isEmpty()) { // Do not append if query is an empty string
            textView.append(query + '\n');
            et_search_ingr.setText(""); // Clear text field when Add button is pressed
        }

    }

    /**
     * Called when the user taps the Clear List button.
     * Clears the ingredients list contained in the textView.
     */
    public void clearList(View view) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
    }

    /**
     * Called when the user taps the Find Recipes button.
     */
    public void findRecipes(View view) {
        // TODO: Write findRecipes method
    }
}
