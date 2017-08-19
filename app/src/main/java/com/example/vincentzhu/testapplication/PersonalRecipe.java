package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.ArrayList;

import static com.example.vincentzhu.testapplication.R.id.ingredientName;
import static com.example.vincentzhu.testapplication.R.id.pic_btn;

public class PersonalRecipe extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PersonalRecipe";

    //user-related widgets
    private Button mPicture;
    private Button mFirebaseBtn;
    private EditText mNameField;
    private EditText mIngField;
    private EditText mInstrField;
    private ImageView imageDisplay;
    private static final int RESULT_IMAGE = 1;
    private Uri selectedImage;
    private ProgressBar progressU;
    Spinner spinner_recipe_type;
    Spinner spinner_cuisine;
    ArrayAdapter<CharSequence> adapter_recipe_type;
    ArrayAdapter<CharSequence>adapter_cuisine;

    //database-related objects
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private StorageReference mStorage;
    private String userID;
    private DatabaseReference mRoot;
    private DatabaseReference mRecipe_Ingredients;
    private DatabaseReference mIngredient_Recipes;
    private DatabaseReference mCuisine_Recipe;
    private DatabaseReference mRecipes;
    private DatabaseReference mType_Recipes;
    private String uid;
    private StorageReference uploadPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recipe);

        //check user is still logged in
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        // Create the toolbar and set it as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //user-related display
        mPicture = (Button) findViewById(R.id.pic_btn);
        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        imageDisplay = (ImageView) findViewById(R.id.imageDisplay);
        spinner_recipe_type = (Spinner)findViewById(R.id.spinner_type);
        spinner_cuisine = (Spinner)findViewById(R.id.spinner_cuisine);
        adapter_recipe_type = ArrayAdapter.createFromResource(this, R.array.recipe_types,android.R.layout.simple_spinner_item);
        adapter_cuisine = ArrayAdapter.createFromResource(this, R.array.recipe_cuisines,android.R.layout.simple_spinner_item);
        spinner_recipe_type.setAdapter(adapter_recipe_type);
        spinner_cuisine.setAdapter(adapter_cuisine);
        mNameField = (EditText)findViewById(R.id.name_field);
        mIngField = (EditText)findViewById(R.id.ing_field);
        mInstrField = (EditText)findViewById(R.id.instr_field);
        mPicture.setOnClickListener(this);
        mFirebaseBtn.setOnClickListener(this);
        progressU=(ProgressBar) findViewById(R.id.progressUpload);
        progressU.setVisibility(View.GONE);

        //database-related
        userID = firebaseAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child(userID);
        mStorage = FirebaseStorage.getInstance().getReference().child("Recipes");
        mRecipe_Ingredients = mRoot.child("Recipe_Ingredients");
        mIngredient_Recipes = mRoot.child("Ingredient_Recipes");
        mCuisine_Recipe = mRoot.child("Cuisine_Recipe");
        mRecipes = mRoot.child("Recipes");
        mType_Recipes = mRoot.child("Type_Recipes");
        user  = firebaseAuth.getCurrentUser();
        uid = UUID.randomUUID().toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.pic_btn:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_IMAGE);
                break;
            case R.id.firebase_btn:
                String recipeName = mNameField.getText().toString().trim();
                String recipeInstruction = mInstrField.getText().toString().trim();
                String recipeIngredients = mIngField.getText().toString() + " "; //need to update later
                ArrayList<String> parse = new ArrayList<String>();
                parseString(recipeIngredients, parse);

                //Method 3
                mCuisine_Recipe.child(spinner_cuisine.getSelectedItem().toString()).child(recipeName).setValue(recipeName);
                //Method 4
                mType_Recipes.child(spinner_recipe_type.getSelectedItem().toString()).child(recipeName).setValue(recipeName);
                //Method 5
                mRecipes.child(recipeName).child("Type").setValue(spinner_recipe_type.getSelectedItem().toString());
                mRecipes.child(recipeName).child("Cuisine").setValue(spinner_cuisine.getSelectedItem().toString());
                mRecipes.child(recipeName).child("Instructions").setValue(recipeInstruction);

                //Method 6 & 7
                for(String word : parse)
                {
                    mRecipe_Ingredients.child(recipeName).child(word).setValue(word);
                    mIngredient_Recipes.child(word).child(recipeName).setValue(recipeName);
                }

                uploadFile(recipeName);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_IMAGE && resultCode==RESULT_OK && data!=null){
            selectedImage = data.getData();
            imageDisplay.setImageURI(selectedImage);
        }
    }

    private void uploadFile (String recipeName) {
        if (selectedImage != null) {
            uploadPath = mStorage.child(user.getEmail()).child(uid);
            Log.i(TAG, uploadPath.toString());
            uploadPath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressU.setVisibility(View.GONE);
                    Toast.makeText(PersonalRecipe.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();

                }
            });

            mRecipes.child(recipeName).child("Image").setValue(uploadPath.toString());
//            uploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    String url = uri.toString();
//                    mRecipes.child(recipeName).child("Image").setValue(url);
//                    Toast.makeText(PersonalRecipe.this, "URL Saved successfully", Toast.LENGTH_LONG).show();
//                }
//            });
        }
    }

    public void parseString(String line, ArrayList<String> parse)
    {
        int startIndex = 0;
        int endIndex = 0;
        boolean found = false;

        for(int i=0; i<line.length(); i++)
        {
            if(line.charAt(i)!= ' ' && found == false)
            {
                startIndex = i;
                found = true;
            }
            else if(line.charAt(i) == ' ' && found == true)
            {
                endIndex = i;
                parse.add(line.substring(startIndex, endIndex));
                found = false;
            }
        }
    }
}

