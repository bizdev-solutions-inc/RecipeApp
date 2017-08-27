package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
                        finish();
                        startActivity(new Intent(Home.this, PersonalRecipe.class));
                        return true;
                    case R.id.add_ing:
                        finish();
                        startActivity(new Intent(Home.this, PersonalIngredient.class));
                        return true;
                    default:
                        return true;
                }
            }
        });

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        String mainAccount = "devbizrecipe@gmail.com";
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user.getEmail().equals(mainAccount)){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),Admin.class));
        }

        // Do not display Up button since this is the Home menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_menu_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the childNames of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // onClick handlers for buttons
//        Button btn_add_ing = (Button) findViewById(R.id.btn_add_ing); //add personal ingredient button
//        btn_add_ing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Home.this, PersonalIngredient.class));
//            }
//        });
//        Button btn_add_recipe = (Button) findViewById(R.id.btn_add_recipe);
//        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Home.this, PersonalRecipe.class));
//            }
//        });

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
            case "Recipe/Ingredient Name":
                startActivity(new Intent(Home.this, SearchByName.class));
                break;
            case "Recipe Type":
                intent = new Intent(Home.this, SearchByRecipeAttribute.class);
                intent.putExtra("RECIPE_ATTRIBUTE", "Type_Recipes");
                startActivity(intent);
                break;
            case "Recipe Cuisine":
                intent = new Intent(Home.this, SearchByRecipeAttribute.class);
                intent.putExtra("RECIPE_ATTRIBUTE", "Cuisine_Recipe");
                startActivity(intent);
                break;
            case "Ingredient List":
                startActivity(new Intent(Home.this, IngredientList.class));
                break;
            case "Ingredient Category":
                startActivity(new Intent(Home.this, IngredientCatalog.class));
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

    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }


}
