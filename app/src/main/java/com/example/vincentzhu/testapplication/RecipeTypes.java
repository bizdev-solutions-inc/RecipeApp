package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipeTypes extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recipe_types);
        super.onCreate(savedInstanceState);

        // Set ListView items by using an adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeTypes.this,
                R.array.recipe_types, android.R.layout.simple_list_item_1);
        ListView lv_recipe_types = (ListView) findViewById(R.id.lv_recipe_types);
        lv_recipe_types.setAdapter(adapter);
        lv_recipe_types.setOnItemClickListener(itemClickListener);
    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // TODO: Search for recipes based on recipe type chosen
                }
            };
}
