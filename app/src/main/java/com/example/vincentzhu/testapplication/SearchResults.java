package com.example.vincentzhu.testapplication;

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
import java.util.Iterator;

public class SearchResults extends BaseActivity
        implements FilterResultsDialogFragment.FilterResultsDialogListener{

    private ArrayList<String> recipe_list;
    private enum Attribute {TYPE, CUISINE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        recipe_list = (ArrayList<String>) intent.getSerializableExtra("RECIPE_RESULTS");

        updateList();
    }

    // Create a list-item click-handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    displayItem(recipe_list.get(position));
                }
            };

    /**
     * Called when the user selects an item from the search results list.
     * This takes the user to the RecipePage activity that displays information about
     * the recipe or ingredient selected by the user.
     * @param item
     */
    public void displayItem(String item) {
        Intent intent = new Intent(SearchResults.this, RecipePage.class);
        intent.putExtra("SELECTED_ITEM", item);
        startActivity(intent);
    }

    /**
     * Updates the ListView with the search results entries.
     * Should be called after changes to the entries, like after filtering.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, recipe_list);
        ListView lv_search_results = (ListView) findViewById(R.id.lv_search_results);
        lv_search_results.setAdapter(adapter);
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
                                    String recipe_type, String recipe_cuisine, String ingredient) {
        if (filterOptionsChecked[0]) { // Filter by recipe type
            filterByAttribute(Attribute.TYPE, recipe_type);
        }
        if (filterOptionsChecked[1]) { // Filter by recipe cuisine
            filterByAttribute(Attribute.CUISINE, recipe_cuisine);
        }
        if (filterOptionsChecked[2]) { // Filter by excluding an ingredient
            excludeIngredient(ingredient);
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
     * Filter the search results by the attribute specified by the user's filter option choices.
     * Can filter by either recipe type or recipe cuisine.
     * @param attribute
     * @param filter
     */
    private void filterByAttribute(Attribute attribute, final String filter) {
        String childName = "";

        if (attribute == Attribute.TYPE) {
            childName = "Type";
        } else if (attribute == Attribute.CUISINE) {
            childName = "Cuisine";
        }
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().child("Recipes");

        final String attr = childName;
        mRoot.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<String> iter = recipe_list.iterator();

                    while (iter.hasNext()) {
                        String recipe = iter.next();
                        // If a recipe in the list does not match the attribute specified,
                        // remove it from the search results
                        if (!dataSnapshot.child(recipe).child(attr).getValue().equals(filter)) {
                            iter.remove();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        updateList();
    }

    /**
     * Filter the search results based on the ingredient specified by the user.
     * All recipes in the search results containing the ingredient specified
     * are removed from the search results.
     * @param ingredient
     */
    private void excludeIngredient(final String ingredient) {
//        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference()
//                .child("Recipe_Ingredients");
//        for (final String recipe : recipe_list) {
//            mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // Any recipes in the search results containing the ingredient specified
//                    // by the user are removed from the search results
//                    for (DataSnapshot ds : dataSnapshot.child(recipe).getChildren()) {
//                        if (ds.equals(ingredient)) {
//                            recipe_list.remove(recipe);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//        updateList(); // Update search results with filtered results
    }
}
