package com.bizdev.recipeapp.cookitup;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResults extends BaseActivity
        implements FilterResultsDialogFragment.FilterResultsDialogListener{

    private ArrayList<String> recipe_list;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        // Get the recipe results from the activity that called this one
        Intent intent = getIntent();
        recipe_list = (ArrayList<String>) intent.getSerializableExtra("RECIPE_RESULTS");

        // Initialize the adapter with the list of recipe results
        adapter = new ArrayAdapter<>(SearchResults.this,
                android.R.layout.simple_list_item_1, recipe_list);

        // Set up the ListView, its adapter, and its OnItemClickListener
        ListView lv_search_results = findViewById(R.id.lv_search_results);
        lv_search_results.setAdapter(adapter);
        lv_search_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                displayItem(recipe_list.get(i));
            }
        });
    }

    /**
     * Called when the user selects an item from the search results list.
     * This takes the user to the RecipePage or IngredientPage activity that displays information
     * about the recipe or ingredient selected by the user.
     * @param item The selected item to be displayed in its profile page
     */
    public void displayItem(String item) {
        Intent intent = new Intent(SearchResults.this, RecipePage.class);
        intent.putExtra("SELECTED_ITEM", item);
        startActivity(intent);
    }

    /**
     * Called when the user taps the Filter Results button to filter their search results.
     * This shows the user a dialog with options to filter their results.
     * @param view
     */
    public void showFiltersDialog(View view) {
        FilterResultsDialogFragment filterDialog = new FilterResultsDialogFragment();
        filterDialog.show(getFragmentManager(), "FilterDialog");
    }

    /**
     * Called when user taps the Filter button in the Filter Results dialog.
     * Filters the search results according to the options selected by the user.
     * @param dialog
     */
    @Override
    public void onDialogFilterClick(DialogFragment dialog, boolean [] filterOptionsChecked,
                                    String recipe_type, String recipe_cuisine,
                                    String ingredient_to_exclude) {
        if (filterOptionsChecked[0]) { // Filter by recipe type
            filterByType(recipe_type);
        }
        if (filterOptionsChecked[1]) { // Filter by recipe cuisine
            filterByCuisine(recipe_cuisine);
        }
        if (filterOptionsChecked[2]) { // Filter by excluding an ingredient
            excludeIngredient(ingredient_to_exclude);
        }
    }

    /**
     * Called when user taps the Cancel button in the Filter Results dialog.
     * Closes the dialog and does nothing.
     * @param dialog
     */
    @Override
    public void onDialogCancelClick(DialogFragment dialog) {

    }

    /**
     * Filter the search results by the type of recipe specified by the user's
     * filter option choices. All search results that do not match the specified
     * type of recipe are removed from the list.
     * @param recipe_type The type of recipe to be filtered
     */
    private void filterByType(final String recipe_type) {
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> filtered_results = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String recipe : recipe_list) {
                    if (dataSnapshot.child(recipe).child("Type").getValue().equals(recipe_type)) {
                        filtered_results.add(recipe);
                    }
                }
                recipe_list.retainAll(filtered_results);
                adapter.notifyDataSetChanged(); // Update the ListView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Filter the search results by the cuisine specified by the user's
     * filter option choices. All search results that do not match the specified
     * cuisine are removed from the list.
     * @param recipe_cuisine The specified cuisine to be filtered
     */
    private void filterByCuisine(final String recipe_cuisine) {
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> filtered_results = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String recipe : recipe_list) {
                    if (dataSnapshot.child(recipe).child("Cuisine").getValue()
                            .equals(recipe_cuisine)) {
                        filtered_results.add(recipe);
                    }
                }
                recipe_list.retainAll(filtered_results);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Filter the search results based on the ingredient specified by the user.
     * All recipes in the search results containing the ingredient specified
     * are removed from the search results.
     * @param ingredient_to_exclude Recipes containing this ingredient are filtered out of the
     *                              search results
     */
    private void excludeIngredient(final String ingredient_to_exclude) {
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference()
                .child("Recipe_Ingredients");
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> contain_ingredient = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if recipe contains the ingredient to exclude.
                // If it does, add it to the contain_ingredients ArrayList
                for (DataSnapshot recipe : dataSnapshot.getChildren()) {
                    for (DataSnapshot ingredient : recipe.getChildren()) {
                        if (ingredient.getValue().equals(ingredient_to_exclude)) {
                            contain_ingredient.add(recipe.getKey());
                            break;
                        }
                    }
                }
                // Remove all recipes that contain the ingredient to be excluded
                recipe_list.removeAll(contain_ingredient);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
