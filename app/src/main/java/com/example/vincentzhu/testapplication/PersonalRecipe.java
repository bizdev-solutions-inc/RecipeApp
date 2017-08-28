package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
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

import static com.example.vincentzhu.testapplication.R.id.imageDisplay;

public class PersonalRecipe extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PersonalRecipe";

    //user-related widgets
//    private Button mFirebaseBtn;
    private EditText mNameField;
    private EditText mInstrField;
    private static final int RESULT_IMAGE = 1;
    private static final int CAPTURE_CAMERA = 11;
    private Uri selectedImage;
    Spinner spinner_recipe_type;
    Spinner spinner_recipe_cuisine;
    ArrayAdapter<CharSequence> adapter_recipe_type;
    ArrayAdapter<CharSequence> adapter_recipe_cuisine;
    AutoCompleteTextView actv_ingredients;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_recipe);
        super.onCreate(savedInstanceState);

        //user-related display
//        mFirebaseBtn = findViewById(R.id.firebase_btn);
        spinner_recipe_type = findViewById(R.id.spinner_type);
        spinner_recipe_cuisine = findViewById(R.id.spinner_cuisine);
        adapter_recipe_type = ArrayAdapter.createFromResource(this, R.array.recipe_types,android.R.layout.simple_spinner_item);
        adapter_recipe_cuisine = ArrayAdapter.createFromResource(this, R.array.recipe_cuisines,android.R.layout.simple_spinner_item);
        spinner_recipe_type.setAdapter(adapter_recipe_type);
        spinner_recipe_cuisine.setAdapter(adapter_recipe_cuisine);
        mNameField = findViewById(R.id.name_field);
        mInstrField = findViewById(R.id.instr_field);
        imageDisplayRec = (ImageView) findViewById(imageDisplay);
//        mFirebaseBtn.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ListView listView = findViewById(R.id.lv_added_ingredients);
        listView.setOnItemClickListener(itemClickListener);

        actv_ingredients = findViewById(R.id.actv_ingredients);
        all_ingredients = new ArrayList<String>();
        ingredient_list = new ArrayList<String>();
        ArrayAdapter<String> actvAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, all_ingredients);
        actv_ingredients.setAdapter(actvAdapter);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.ing_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pic_btn:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, RESULT_IMAGE);
                        return true;
                    case R.id.firebase_ing_btn:
//                        finish();
//                        startActivity(new Intent(Home.this, PersonalIngredient.class));
                        saveIngData ();
                        return true;
                    case R.id.take_photo:
                        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAPTURE_CAMERA);
//                        }
                        return true;
                    default:
                        return true;
                }
            }
        });

        //database-related
        firebaseAuth = FirebaseAuth.getInstance();
        user  = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        mRoot = FirebaseDatabase.getInstance().getReference().child("Ingredient_Recipes");
        mStorage = FirebaseStorage.getInstance().getReference().child("Recipes");
        mUser = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Recipes");
        mRecipe_Ingredients = mUser.child("Recipe_Ingredients");
        mIngredient_Recipes = mUser.child("Ingredient_Recipes");
        mCuisine_Recipe = mUser.child("Cuisine_Recipe");
        mRecipes = mUser.child("Recipes");
        mType_Recipes = mUser.child("Type_Recipes");

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

    public void saveIngData () {

        String recipeName = mNameField.getText().toString().trim();
        String recipeInstruction = mInstrField.getText().toString().trim();
        String recipeCuisine = spinner_recipe_cuisine.getSelectedItem().toString();
        String recipeType = spinner_recipe_type.getSelectedItem().toString();

        if (spinner_recipe_cuisine.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a valid cuisine", Toast.LENGTH_SHORT).show();
        } else if (spinner_recipe_type.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a valid type", Toast.LENGTH_SHORT).show();
        } else {
            mCuisine_Recipe.child(recipeCuisine).child(recipeName).setValue(recipeName);
            mRecipes.child(recipeName).child("Cuisine").setValue(recipeCuisine);
            mRecipes.child(recipeName).child("Instructions").setValue(recipeInstruction);
            mType_Recipes.child(recipeType).child(recipeName).setValue(recipeName);
            mRecipes.child(recipeName).child("Type").setValue(recipeType);

            for (String word : ingredient_list) {
                mRecipe_Ingredients.child(recipeName).child(word).setValue(word);
                mIngredient_Recipes.child(word).child(recipeName).setValue(recipeName);
            }
            uploadFile(recipeName);

        }
    }

    @Override
    public void onClick(View v) {
//      switch(v.getId()) {
//           case R.id.pic_btn:
//      Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//              galleryIntent.setType("image/*");
//               startActivityForResult(galleryIntent, RESULT_IMAGE);
//               break;
//          case R.id.firebase_btn:
//               String recipeName = mNameField.getText().toString().trim();
//              String recipeInstruction = mInstrField.getText().toString().trim();
//               String recipeCuisine = spinner_recipe_cuisine.getSelectedItem().toString();
//               String recipeType = spinner_recipe_type.getSelectedItem().toString();
//
//               if(spinner_recipe_cuisine.getSelectedItemPosition()==0)
//                {
//                    Toast.makeText(this, "Please select a valid cuisine", Toast.LENGTH_SHORT).show();
//                }
//                else if(spinner_recipe_type.getSelectedItemPosition()==0)
//                {
//                    Toast.makeText(this, "Please select a valid type", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    mCuisine_Recipe.child(recipeCuisine).child(recipeName).setValue(recipeName);
//                    mRecipes.child(recipeName).child("Cuisine").setValue(recipeCuisine);
//                    mRecipes.child(recipeName).child("Instructions").setValue(recipeInstruction);
//                    mType_Recipes.child(recipeType).child(recipeName).setValue(recipeName);
//                    mRecipes.child(recipeName).child("Type").setValue(recipeType);

//                    for(String word : ingredient_list)
//                    {
//                        mRecipe_Ingredients.child(recipeName).child(word).setValue(word);
//                        mIngredient_Recipes.child(word).child(recipeName).setValue(recipeName);
//                    }
//                    uploadFile(recipeName);
//                }
//                break;
//        }
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
        if (requestCode==RESULT_IMAGE && resultCode==RESULT_OK && data!=null){
            switch (requestCode){
                case CAPTURE_CAMERA:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageDisplayRec.setImageBitmap(imageBitmap);
                    break;
                case  RESULT_IMAGE:
                    selectedImage = data.getData();
                    imageDisplayRec.setImageURI(selectedImage);
                    break;
            }
        }
    }

    private void uploadFile (final String recipeName) {
        uid = UUID.randomUUID().toString();
        if (selectedImage != null) {
            uploadPath = mStorage.child(user.getUid()).child(uid);
            Log.i(TAG, uploadPath.toString());
            uploadPath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PersonalRecipe.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();
                }
            });
            mRecipes.child(recipeName).child("Image").setValue(uploadPath.toString());
        }
    }


//    public void parseString(String line, ArrayList<String> parse)
//    {
//        int startIndex = 0;
//        int endIndex = 0;
//        boolean found = false;
//
//        //separate ingredients by comma
//        for(int i=0; i<line.length(); i++)
//        {
//            if(line.charAt(i)!= ',' && line.charAt(i)!= ' ' && found == false)
//            {
//                startIndex = i;
//                found = true;
//            }
//            else if((line.charAt(i) == ',' || i == line.length()-1) && found == true)
//            {
//                if(line.charAt(line.length()-1) == ' ')
//                    endIndex = i-1;
//                else
//                    endIndex = i;
//                parse.add(line.substring(startIndex, endIndex));
//                found = false;
//            }
//        }
//    }

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
    /**
     * Updates the ListView with entries from the ingredients list.
     * Should be called every time an entry is added to or removed from the list.
     * This method uses an ArrayAdapter to retrieve data from the ingredients ArrayList
     * and display each String entry as an item in the ListView.
     */
    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ingredient_list);
        ListView lv_added_ingredients = (ListView) findViewById(R.id.lv_added_ingredients);
        lv_added_ingredients.setAdapter(adapter);
    }
}

