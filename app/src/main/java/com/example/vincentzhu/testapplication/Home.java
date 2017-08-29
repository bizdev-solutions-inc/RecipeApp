package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseUser;

public class Home extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user.getEmail().equals(mainAccount)){
            //Profile activity here
            finish();
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
                intent = new Intent(Home.this, SearchByName.class);
                intent.putExtra("SEARCH_NAME", "Recipes");
                startActivity(intent);
                break;
            case "Recipe Type":
                parent.setSelection(0);
                intent = new Intent(Home.this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Type_Recipes");
                startActivity(intent);
                break;
            case "Recipe Cuisine":
                parent.setSelection(0);
                intent = new Intent(Home.this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Cuisine_Recipe");
                startActivity(intent);
                break;
            case "Ingredient List":
                parent.setSelection(0);
                startActivity(new Intent(Home.this, IngredientList.class));
                break;
            case "Ingredient Name":
                parent.setSelection(0);
                intent = new Intent(Home.this, SearchByName.class);
                intent.putExtra("SEARCH_NAME", "Ingredients");
                startActivity(intent);
                break;
            case "Ingredient Catalog":
                parent.setSelection(0);
                intent = new Intent(Home.this, SearchByAttribute.class);
                intent.putExtra("ATTRIBUTE", "Type_Ingredients");
                startActivity(intent);
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

    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }


}
