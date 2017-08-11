package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SavedRecipe extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipe);
        String info = "";
        TextView view = (TextView)findViewById(R.id.recipeInfo);
        SharedPreferences sharedPref = getSharedPreferences("recipeInfo", Context.MODE_PRIVATE);
        int index = sharedPref.getInt("index", 0);

        for(int i = 1; i <= index; i++) {

            info = info + "Favorites " + i + ": " + sharedPref.getString("Favorite" + i, "") + "\n\n";
        }
        view.setText(info);
    }
}
