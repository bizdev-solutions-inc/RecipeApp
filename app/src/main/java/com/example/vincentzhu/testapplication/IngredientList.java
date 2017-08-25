package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This activity displays the list of ingredients entered by the user.
 * The user is able to add and remove ingredients from the list or
 * goToSearch for recipes that use the ingredients in the list.
 */
public class IngredientList extends BaseActivity {

    private ArrayList<String> ingredient_list, recipe_list, favorites_list;
    private DatabaseReference mRoot;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ingredient_list);
        super.onCreate(savedInstanceState);

        ingredient_list = new ArrayList<String>(); // initialize list of ingredients
        favorites_list = new ArrayList<String>();
        ArrayList<String> real_favorites = new ArrayList<String>();

        favorites_list.add("Favorites");
        populateFavorites();

        // Set the ListView's OnItemClickListener to handle clicking to remove list items
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClickListener);

        Spinner sp_favorites = (Spinner) findViewById(R.id.sp_favorites);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, favorites_list);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_favorites.setAdapter(adapter);
        sp_favorites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (!item.equals("Favorites")){
                    ingredient_list.add(item);
                    updateList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     *
     */
    private void populateFavorites() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Favorited").child(userid).exists()) {

                        favorites_list.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     */
    public void search(View view) {
        mRoot = FirebaseDatabase.getInstance().getReference();

        mRoot.child("Ingredient_Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe_list = queryData(dataSnapshot);
                Intent intent = new Intent(IngredientList.this, SearchResults.class);
                intent.putExtra("RECIPE_RESULTS", recipe_list);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * @param dataSnapshot
     */
    public ArrayList<String> queryData(DataSnapshot dataSnapshot) {

        HashSet<String> recipes = new HashSet<>(), ingredients = new HashSet<>(ingredient_list);

        for (String currentIngredient : ingredients) {
            try {
                HashMap<String, Object> current =
                        (HashMap) dataSnapshot.child(currentIngredient).getValue();

                if (recipes.isEmpty())
                    recipes.addAll(current.keySet());
                else {
                    HashSet<String> currentRecipes = new HashSet<>(current.keySet());
                    recipes.retainAll(currentRecipes);
                }
            } catch (Exception e) {
                Log.d("Ingredient not found: ", currentIngredient);
            }
        }
//        this.recipe_list = new ArrayList<>(recipes);
        return new ArrayList<String>(recipes);
    }

    // Create a list-item click-handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                // When the user clicks on a list item, it is removed from the list.
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    ingredient_list.remove(position);
                    updateList();
                }
            };


    /**
     * Called when the user taps the Add button.
     * Adds the ingredient entered by the user to the ingredients list.
     */
    public void addIngredient(View view) {
        EditText et_search_ingr = (EditText) findViewById(R.id.et_search_ingr);
        String query = et_search_ingr.getText().toString();
        if (!query.isEmpty()) { // Do not append if query is an empty string
            et_search_ingr.setText(""); // Clear text field when Add button is pressed
            ingredient_list.add(query);
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
                android.R.layout.simple_list_item_1, ingredient_list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /**
     * Called when the user taps the Clear List button.
     * Clears the ingredients list contained in the ListView.
     */
    public void clearList(View view) {
        ingredient_list.clear();
        updateList();
    }

//    /**
//     * Called when the user taps the Find Recipes button.
//     * Sends the list of ingredients entered by the user as an Extra
//     * to the SearchResults activity.
//     */
//    public void findRecipes(View view) {
//        // go to SearchResults activity
//        Intent intent = new Intent(IngredientList.this, SearchResults.class);
//        intent.putExtra("ingredientlist", ingredient_list);
//        startActivity(intent);
//    }
}
