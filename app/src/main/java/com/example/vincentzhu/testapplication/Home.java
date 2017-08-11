package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    // Test comment, delete later
    // Test comment 2, delete later

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Wire up the button to do stuff
        //..get the button
        Button btn = (Button)findViewById(R.id.btnIC); //type cast
        Button addRecipe = (Button)findViewById(R.id.addrecipe);
        //..set what happens when the user clicks
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i(TAG, "This is a magic log message!");
                //Toast.makeText(getApplicationContext(), "It's magic!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Home.this, Ingredient_Categories.class));
                //setTitle("Ingredient Categories");
            }
        });

        addRecipe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, PersonalRecipe.class));
            }
        });

    }

    public void displaySavedRecipe(View view)
    {
        startActivity(new Intent(Home.this, SavedRecipe.class));
    }

    public void displaySavedIngredients(View view)
    {
        startActivity(new Intent(Home.this, SavedIngredients.class));
    }
}
