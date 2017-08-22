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
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class Ingredient_Categories extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ingredient_categories);
        super.onCreate(savedInstanceState);

//        Button btn_meat_seafood = (Button) findViewById(R.id.btn_meat_seafood); //type cast

//        //..set what happens when the user clicks
//        btn_meat_seafood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Log.i(TAG, "This is a magic log message!");
//                //Toast.makeText(getApplicationContext(), "It's magic!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Ingredient_Categories.this, MeatScroll.class));
//                //setTitle("Ingredient Categories");
//            }
//        });

        // Set ListView items by using an adapter
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(Ingredient_Categories.this,
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
                            startActivity(new Intent(Ingredient_Categories.this, MeatScroll.class));
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
