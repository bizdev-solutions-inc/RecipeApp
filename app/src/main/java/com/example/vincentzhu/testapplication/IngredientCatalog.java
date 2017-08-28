package com.example.vincentzhu.testapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IngredientCatalog extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ingredient_categories);
        super.onCreate(savedInstanceState);

        // Set ListView items by using an adapter
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(IngredientCatalog.this,
                        R.array.ingredient_types,
                        android.R.layout.simple_list_item_1);
        ListView lv_recipe_types = (ListView) findViewById(R.id.lv_ingredient_types);
        lv_recipe_types.setAdapter(adapter);
        lv_recipe_types.setOnItemClickListener(itemClickListener);

    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // TODO: Search for recipes based on ingredient type chosen
                    String item = parent.getItemAtPosition(position).toString();
                    switch (item) {
                        case "Meat":
                            break;
                        case "Vegetable":
                            break;
                        case "Fruit":
                            break;
                        case "Dairy":
                            break;
                        case "Grain":
                            break;
                        case "Miscellaneous":
                            break;
                        default:
                            break;
                    }
                }
            };

}
