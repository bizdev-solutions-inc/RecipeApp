package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    // Key for ingredient entered by user
    public static final String EXTRA_INGREDIENT = "INGREDIENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is still logged in. If not, return to Login activity
        if(firebaseAuth.getCurrentUser()==null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        //Wire up the button to do stuff
        //..get the button
        Button btn = (Button) findViewById(R.id.btn_ingr_categories); //type
        Button addIng = (Button)findViewById(R.id.btn_add_ing); //add personal ingredient button
        Button addRecipe = (Button) findViewById(R.id.btn_add_recipe);
        //..set what happens when the user clicks
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Ingredient_Categories.class));
                //setTitle("Ingredient Categories");
            }
        });

        addIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, PersonalIngredient.class));
            }
        });

        addRecipe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, PersonalRecipe.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_search:
//                // User chose the "Search" item, show search dialog
//                startActivity(new Intent(this, IngredientsList.class));
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

    /**
     * Called when the user taps the Add button.
     * Sends the ingredient entered by the user to the IngredientsList activity.
     */
    public void addIngredient(View view) {
        Intent intent = new Intent(this, IngredientsList.class);
        EditText et_search_ingr = (EditText) findViewById(R.id.et_search_ingr);
        String query = et_search_ingr.getText().toString();
        if (!query.isEmpty()) { // Do nothing if query is an empty string
            et_search_ingr.setText(""); // Clear text field when Add button is pressed
            intent.putExtra(EXTRA_INGREDIENT, query);
            startActivity(intent);
        }
    }

    public void displaySavedRecipe(View view)
    {
        startActivity(new Intent(Home.this, SavedRecipe.class));
    }

    public void displaySavedIngredients(View view)
    {
        startActivity(new Intent(Home.this, SavedIngredients.class));
    }


}
