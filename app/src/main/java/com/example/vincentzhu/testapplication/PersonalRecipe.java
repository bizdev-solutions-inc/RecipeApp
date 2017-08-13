package com.example.vincentzhu.testapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalRecipe extends AppCompatActivity {

//    EditText nametext1;
//    EditText nametext2;
//    EditText nametext3;

    private FirebaseAuth firebaseAuth;

    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    private EditText mNameField;
    private EditText mIngField;
    private EditText mInstrField;

    private DatabaseReference mName;
    private DatabaseReference mIng;
    private DatabaseReference mInstr;
    private DatabaseReference mCuisine;
    private DatabaseReference mMealType;

    //public static int index = 1;

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

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

//        nametext1 = (EditText)findViewById(R.id.editText1);
//        nametext2 = (EditText)findViewById(R.id.editText2);
//        nametext3 = (EditText)findViewById(R.id.editText3);

        mFirebaseBtn = (Button)findViewById(R.id.firebase_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Recipe Name");

        mNameField = (EditText)findViewById(R.id.name_field);
        mIngField = (EditText)findViewById(R.id.ing_field);
        mInstrField = (EditText)findViewById(R.id.instr_field);

        mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mNameField.getText().toString().trim();
                String ing = mIngField.getText().toString().trim();
                String instr = mInstrField.getText().toString().trim();

                mName = mDatabase.child(name);
                mIng = mName.child("Ingredients");
                mCuisine = mName.child("Cuisine");
                mMealType = mName.child("Meal Type");
                mInstr = mName.child("Instructions");
                mIng.push().setValue(ing);
                mInstr.push().setValue(instr);
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Search" item, show search dialog
                onSearchRequested();
                return true;
            case R.id.action_home:
                // User chose the "Home" item, show the Home activity
                finish();
                startActivity(new Intent(this, Home.class));
                return true;
            case R.id.action_about_us:
                // User chose the "About Us" item, show the About Us activity
                finish();
                startActivity(new Intent(this, AboutUs.class));
                return true;
            case R.id.action_logout:
                // User chose the "Log Out" item, log the user out and return to login activity
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                // The user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

//    public void saveRecipe(View view)
//    {
//        SharedPreferences sharedPref = getSharedPreferences("recipeInfo", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//
//        editor.putString("Favorite" + index, "\nRecipe Name:\n" + nametext1.getText().toString()
//        + "\nIngredients List:\n" + nametext2.getText().toString() + "\nCooking Instructions:\n" + nametext3.getText().toString());
//        editor.putInt("index", index);
//        editor.commit();
//        Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_LONG).show();
//        index++;
//    }
}
