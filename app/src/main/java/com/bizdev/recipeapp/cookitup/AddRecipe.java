package com.bizdev.recipeapp.cookitup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import static com.bizdev.recipeapp.cookitup.R.id.imageDisplay;

public class AddRecipe extends BaseActivity {

    private static final int RESULT_IMAGE = 1;
    private static final int CAPTURE_CAMERA = 11;
    Spinner spinner_recipe_type;
    Spinner spinner_recipe_cuisine;
    ArrayAdapter<CharSequence> adapter_recipe_type;
    ArrayAdapter<CharSequence> adapter_recipe_cuisine;
    AutoCompleteTextView actv_ingredients;
    //user-related widgets
    private EditText mNameField;
    private EditText mInstrField;
    private Uri selectedImage;
    //database-related objects
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ImageView imageDisplayRec;
    private StorageReference mStorage;
    private String userID;
    private DatabaseReference mRoot;
    private DatabaseReference mUser;
    private DatabaseReference mRecipe_Ingredients;
    private DatabaseReference mIngredient_Recipes;
    private DatabaseReference mCuisine_Recipe;
    private DatabaseReference mRecipes;
    private DatabaseReference mType_Recipes;
    private String uid;
    private StorageReference uploadPath;
    private ArrayList<String> all_ingredients, ingredient_list;
    // Create a list-item click-handling object as an anonymous class.
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                // When the user clicks on a list item, it is removed from the list.
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                    ingredient_list.remove(position);
                    updateList();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_recipe);
        super.onCreate(savedInstanceState);

        //user-related display
        spinner_recipe_type = findViewById(R.id.spinner_type);
        spinner_recipe_cuisine = findViewById(R.id.spinner_cuisine);
        adapter_recipe_type = ArrayAdapter.createFromResource
                (this, R.array.recipe_types, android.R.layout.simple_spinner_item);
        adapter_recipe_cuisine = ArrayAdapter.createFromResource
                (this, R.array.recipe_cuisines, android.R.layout.simple_spinner_item);
        spinner_recipe_type.setAdapter(adapter_recipe_type);
        spinner_recipe_cuisine.setAdapter(adapter_recipe_cuisine);
        mNameField = findViewById(R.id.name_field);
        mInstrField = findViewById(R.id.instr_field);
        imageDisplayRec = findViewById(imageDisplay);

        ListView listView = findViewById(R.id.lv_favorite_recipes);
        listView.setOnItemClickListener(itemClickListener);

        actv_ingredients = findViewById(R.id.actv_ingredients);
        all_ingredients = new ArrayList<>();
        ingredient_list = new ArrayList<>();
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, all_ingredients);
        actv_ingredients.setAdapter(actvAdapter);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        BottomNavigationView bottomNavigationView = findViewById(R.id.ing_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pic_btn:
                                Intent galleryIntent = new Intent
                                        (Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, RESULT_IMAGE);
                                return true;
                            case R.id.firebase_ing_btn:
                                saveRecData();
                                return true;
                            case R.id.take_photo:
                                Intent takePictureIntent
                                        = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePictureIntent, CAPTURE_CAMERA);
                                return true;
                            default:
                                return true;
                        }
                    }
                });

        //database-related
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredient_Recipes");

        populateIngredients();

        actv_ingredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = actv_ingredients.getText().toString();
                if (!item.isEmpty() && !ingredient_list.contains(item)) {
                    ingredient_list.add(item);
                    updateList();
                    actv_ingredients.setText("");
                }
            }
        });
    }

    public void saveRecData() {

        String recipeName = mNameField.getText().toString().trim();
        String recipeInstruction = mInstrField.getText().toString().trim();
        String recipeCuisine = spinner_recipe_cuisine.getSelectedItem().toString();
        String recipeType = spinner_recipe_type.getSelectedItem().toString();

        if (TextUtils.isEmpty(recipeName)) {
            //Recipe is empty
            Toast.makeText(this, "Please enter recipe name", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;
        }

        if (TextUtils.isEmpty(recipeInstruction)) {
            //Instruction is empty
            Toast.makeText(this, "Please enter recipe instruction", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;
        }

        if (ingredient_list.isEmpty()) {
            //Ingredient list is empty
            Toast.makeText(this, "Please enter ingredient", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;

        }

        //user tree
        mUser = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Recipes");
        mStorage = FirebaseStorage.getInstance().getReference().child("Recipes");
        mRecipe_Ingredients = mUser.child("Recipe_Ingredients");
        mIngredient_Recipes = mUser.child("Ingredient_Recipes");
        mCuisine_Recipe = mUser.child("Cuisine_Recipe");
        mRecipes = mUser.child("Recipes");
        mType_Recipes = mUser.child("Type_Recipes");

        saveRecipe(recipeName, recipeInstruction, recipeCuisine, recipeType, false);

        //default tree
        mUser = FirebaseDatabase.getInstance().getReference();
        mRecipe_Ingredients = mUser.child("Recipe_Ingredients");
        mIngredient_Recipes = mUser.child("Ingredient_Recipes");
        mCuisine_Recipe = mUser.child("Cuisine_Recipe");
        mRecipes = mUser.child("Recipes");
        mType_Recipes = mUser.child("Type_Recipes");

        saveRecipe(recipeName, recipeInstruction, recipeCuisine, recipeType, true);
    }

    public void saveRecipe(String recipeName, String recipeInstruction, String recipeCuisine,
                           String recipeType, boolean admin) {
        mCuisine_Recipe.child(recipeCuisine).child(recipeName).setValue(recipeName);
        mRecipes.child(recipeName).child("Cuisine").setValue(recipeCuisine);
        mRecipes.child(recipeName).child("Instructions").setValue(recipeInstruction);
        mType_Recipes.child(recipeType).child(recipeName).setValue(recipeName);
        mRecipes.child(recipeName).child("Type").setValue(recipeType);

        for (String word : ingredient_list) {
            mRecipe_Ingredients.child(recipeName).child(word).setValue(word);
            mIngredient_Recipes.child(word).child(recipeName).setValue(recipeName);
        }
        uploadFile(recipeName, admin);
    }

    private void populateIngredients() {
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    all_ingredients.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case CAPTURE_CAMERA:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageDisplayRec.setImageBitmap(imageBitmap);
                    break;
                case RESULT_IMAGE:
                    selectedImage = data.getData();
                    imageDisplayRec.setImageURI(selectedImage);
                    break;
            }
        }
    }

    private void uploadFile(final String recipeName, boolean admin) {
        uid = UUID.randomUUID().toString();
        if (selectedImage != null) {
            if (!admin) {
                uploadPath = mStorage.child(user.getUid()).child(uid);
            } else {
                uploadPath = mStorage.child("lRxFd3PSkGNfeUfZ3qOfpSRoaO12").child(uid);
            }
            uploadPath.putFile(selectedImage).addOnSuccessListener
                    (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddRecipe.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();
                        }
                    });
            mRecipes.child(recipeName).child("Image").setValue(uploadPath.toString());
        }
    }

    /**
     * Updates the ListView with entries from the ingredients list.
     * Should be called every time an entry is added to or removed from the list.
     * This method uses an ArrayAdapter to retrieve data from the ingredients ArrayList
     * and display each String entry as an item in the ListView.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredient_list);
        ListView lv_added_ingredients = findViewById(R.id.lv_favorite_recipes);
        lv_added_ingredients.setAdapter(adapter);
    }
}
