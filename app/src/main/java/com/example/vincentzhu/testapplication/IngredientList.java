package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

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

/**
 * This activity displays the list of ingredients entered by the user.
 * The user is able to add and remove ingredients from the list or
 * goToSearch for recipes that use the ingredients in the list.
 */
public class IngredientList extends BaseActivity {

    private ArrayList<String> ingredient_list, recipe_list, favorites_list, all_ingredients, all_recipes;
    private DatabaseReference mRoot;
    private DatabaseReference mCustom;
    private DatabaseReference mFavorite;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userid;
    private AutoCompleteTextView actv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ingredient_list);
        super.onCreate(savedInstanceState);

        ingredient_list = new ArrayList<>(); // initialize list of ingredients
        favorites_list = new ArrayList<>();
        all_ingredients = new ArrayList<>();
        all_recipes = new ArrayList<>();

        populateIngredients();

        favorites_list.add("Favorites");
        populateFavorites();

        // Set the ListView's OnItemClickListener to handle clicking to remove list items
        ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClickListener);

        Spinner sp_favorites = findViewById(R.id.sp_favorites);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, favorites_list);

        // AutoCompleteTextView
        actv = findViewById(R.id.autoCompleteTextView);
        actv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER)
                {
                    return true;
                }
                return false;
            }
        });

        ArrayAdapter<String> actvAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, all_ingredients);
        actv.setAdapter(actvAdapter);
        actv.requestFocus();

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = actv.getText().toString();
                if (!item.isEmpty() && !ingredient_list.contains(item)) {
                    ingredient_list.add(item);
                    updateList();
                    actv.setText("");
                }
            }
        });


        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_favorites.setAdapter(adapter);
        sp_favorites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (!item.equals("Favorites") && !ingredient_list.contains(item)){
                    ingredient_list.add(item);
                    updateList();
                    adapterView.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Populates the ArrayList of all the ingredients in the database by querying the database.
     * The ArrayList is used to populate the AutoCompleteTextView used as a search field.
     */
    private void populateIngredients(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();

        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredient_Recipes");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    all_ingredients.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCustom = FirebaseDatabase.getInstance().getReference().child(userid).child("Added Ingredients").child("Ingredients");
        mCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!all_ingredients.contains(ds.getKey())) {
                        all_ingredients.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Populates the list of the user's favorite ingredients. This list is used to populate the
     * Favorites spinner that provides a drop-down list of all of the user's favorite recipes for
     * easy access when searching for ingredients.
     */
    private void populateFavorites() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Favorited By").child(userid).exists()) {
                        favorites_list.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFavorite = FirebaseDatabase.getInstance().getReference().child(userid).child("Added Ingredients").child("Ingredients");
        mFavorite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!favorites_list.contains(ds.getKey()) && ds.child("Favorited By").child(userid).exists()){
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
     * Called when the user taps the Search button.
     * Searches the database for recipes containing the ingredients specified by the user and sends
     * the results as a String extra to the SearchResults activity to be displayed.
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
     * Queries the database and searches for recipes containing the specified ingredients.
     * Results are partial hits.
     * @param dataSnapshot the DataSnapshot containing the node in the database tree to be searched
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
        return new ArrayList<>(recipes);
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
     * Updates the ListView with entries from the ingredients list.
     * Should be called every time an entry is added to or removed from the list.
     * This method uses an ArrayAdapter to retrieve data from the ingredients ArrayList
     * and display each String entry as an item in the ListView.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
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

}
