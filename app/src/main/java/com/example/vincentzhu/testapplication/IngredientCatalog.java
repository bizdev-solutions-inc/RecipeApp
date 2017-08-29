package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class IngredientCatalog extends BaseActivity {

    private ArrayList<String> ingredient_list;
    private ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_results);
        super.onCreate(savedInstanceState);

        // Get the ingredient results from the activity that called this one
        Intent intent = getIntent();
        ingredient_list = (ArrayList<String>) intent.getSerializableExtra("INGREDIENT_RESULTS");

        for (int i = 0; i < ingredient_list.size(); i++) {
            ingredient_list.set(i, WordUtils.capitalize(ingredient_list.get(i)));
        }

        // Initialize the adapter with the list of ingredient results
        adapter = new ArrayAdapter<>(IngredientCatalog.this,
                android.R.layout.simple_list_item_1, ingredient_list);

        // Set up the ListView, its adapter, and its OnItemClickListener
        ListView lv_search_results = findViewById(R.id.lv_search_results);
        lv_search_results.setAdapter(adapter);
        lv_search_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                displayItem(ingredient_list.get(i).toLowerCase());
            }
        });
    }
    /**
     * Called when the user selects an item from the search results list.
     * This takes the user to the RecipePage activity that displays information about
     * the recipe or ingredient selected by the user.
     * @param item
     */
    public void displayItem(String item) {
        Intent intent = new Intent(IngredientCatalog.this, IngredientPage.class);
        intent.putExtra("SELECTED_INGREDIENT", item);
        startActivity(intent);
    }
}
