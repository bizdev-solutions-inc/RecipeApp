package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Ingredient_Categories extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_categories);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        Button btn = (Button)findViewById(R.id.button); //type cast

        //..set what happens when the user clicks
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i(TAG, "This is a magic log message!");
                //Toast.makeText(getApplicationContext(), "It's magic!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Ingredient_Categories.this, MeatScroll.class));
                //setTitle("Ingredient Categories");
            }
        });
    }
}
