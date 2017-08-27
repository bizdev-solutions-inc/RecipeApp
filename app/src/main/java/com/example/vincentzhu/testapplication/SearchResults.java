package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchResults extends BaseActivity {

    private ArrayList<String> recipe_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        recipe_list = (ArrayList<String>) intent.getSerializableExtra("RECIPE_RESULTS");

        ListView lv_search_results = (ListView) findViewById(R.id.lv_search_results);
        lv_search_results.setOnItemClickListener(itemClickListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResults.this,
                android.R.layout.simple_list_item_1, recipe_list);
        lv_search_results.setAdapter(adapter);
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
     * Called when the user taps the Filter Results button to filter their search results.
     * This shows the user a dialog with options to filter their results.
     * @param view
     */
    public void showFiltersDialog(View view) {
        FilterResultsDialogFragment filterDialog = new FilterResultsDialogFragment();
        filterDialog.show(getFragmentManager(), "FilterDialog");
    }

}
