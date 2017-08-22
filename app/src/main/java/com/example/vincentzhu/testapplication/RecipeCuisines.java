package com.example.vincentzhu.testapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeCuisines extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recipe_cuisines);
        super.onCreate(savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCuisines.this,
                R.array.recipe_cuisines, android.R.layout.simple_list_item_1);
        ListView lv_recipe_cuisines = (ListView) findViewById(R.id.lv_recipe_cuisines);
        lv_recipe_cuisines.setAdapter(adapter);
        lv_recipe_cuisines.setOnItemClickListener(itemClickListener);
    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // TODO: Search for recipes based on recipe cuisine chosen
                }
            };
}
