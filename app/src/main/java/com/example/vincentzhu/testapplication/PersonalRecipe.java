package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PersonalRecipe extends AppCompatActivity {

    EditText nametext1;
    EditText nametext2;
    EditText nametext3;

    private FirebaseAuth firebaseAuth;

    public static int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recipe);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        nametext1 = (EditText)findViewById(R.id.editText1);
        nametext2 = (EditText)findViewById(R.id.editText2);
        nametext3 = (EditText)findViewById(R.id.editText3);

//        Button btn = (Button)findViewById(R.id.enter);


//        btn.setOnClickListener(new View.OnClickListener(){
//            int index = 0;
//            Recipe[] array = new Recipe[10];
//            view1 = (TextView)findViewById(R.id.textView4);
//
//            @Override
//            public void onClick(View view) {
//                if(index < 10) {
//                    array[index] = new Recipe(nametext1.getText().toString(), nametext2.getText().toString(), nametext3.getText().toString());
//
//                    view1.setText("\nRecipe name: " + array[index].getRecipeName() + "\nIngredients: " + array[index].getQuantities() + "\nInstructions: " + array[index].getCookingInstruction());
//                    index++;
//                }
//            }
//        });
    }

    public void saveRecipe(View view)
    {
        SharedPreferences sharedPref = getSharedPreferences("recipeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("Favorite" + index, "\nRecipe Name:\n" + nametext1.getText().toString()
        + "\nIngredients List:\n" + nametext2.getText().toString() + "\nCooking Instructions:\n" + nametext3.getText().toString());
        editor.putInt("index", index);
        editor.commit();
        Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_LONG).show();
        index++;
    }
}
