package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class PersonalIngredient extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PersonalIngredient";

    //user-related widgets
    private Button mPicture;
    private Button mFirebaseBtn;
    private EditText mIngName;
    private EditText mIngDescription;
    private EditText mIngHistory;
    private ImageView imageDisplay;
    private static final int RESULT_IMAGE = 1;
    private Uri selectedImage;
    private ProgressBar progressU;
    Spinner spinner_type;
    Spinner spinner_season;
    ArrayAdapter<CharSequence> adapter_ingredient_type;
    ArrayAdapter<CharSequence> adapter_ingredient_season;

    //database-related objects
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private StorageReference mStorage;
    private String userID;
    private DatabaseReference mRoot;
    private DatabaseReference mIngredients;
    private DatabaseReference mType_Ingredients;
    private String uid;
    private StorageReference uploadPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_ingredient);
        super.onCreate(savedInstanceState);

        //user-related display
        mPicture = (Button) findViewById(R.id.pic_btn);
        mFirebaseBtn = (Button) findViewById(R.id.firebase_ing_btn);
        imageDisplay = (ImageView) findViewById(R.id.imageDisplay);
        spinner_type = (Spinner)findViewById(R.id.spinner_ing_type);
        spinner_season = (Spinner)findViewById(R.id.spinner_ing_season);
        adapter_ingredient_type = ArrayAdapter.createFromResource(this, R.array.ingredient_types, android.R.layout.simple_spinner_item);
        adapter_ingredient_season = ArrayAdapter.createFromResource(this, R.array.ingredient_seasons, android.R.layout.simple_spinner_item);
        spinner_type.setAdapter(adapter_ingredient_type);
        spinner_season.setAdapter(adapter_ingredient_season);
        mIngName = (EditText) findViewById(R.id.ing_name);
        mIngDescription = (EditText) findViewById(R.id.ing_description);
        mIngHistory = (EditText) findViewById(R.id.ing_history);
        mPicture.setOnClickListener(this);
        mFirebaseBtn.setOnClickListener(this);

        //database-related
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        user = firebaseAuth.getCurrentUser();
        uid = UUID.randomUUID().toString();
        mRoot = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Ingredients");;
        mStorage = FirebaseStorage.getInstance().getReference().child("Ingredients");
        mIngredients = mRoot.child("Ingredients");
        mType_Ingredients = mRoot.child("Type_Ingredients");

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.pic_btn:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_IMAGE);
                break;
            case R.id.firebase_ing_btn:
                String ingredientName = mIngName.getText().toString().trim();
                String ingredientDescription = mIngDescription.getText().toString().trim();
                String ingredientHistory = mIngHistory.getText().toString().trim();

                //Method 1
                mIngredients.child(ingredientName).child("Description").setValue(ingredientDescription);
                mIngredients.child(ingredientName).child("Type").setValue(spinner_type.getSelectedItem().toString());
                mIngredients.child(ingredientName).child("History").setValue(ingredientHistory);
                mIngredients.child(ingredientName).child("Season").setValue(spinner_season.getSelectedItem().toString());

                //Method 2
                mType_Ingredients.child(spinner_type.getSelectedItem().toString()).child(ingredientName).setValue(ingredientName);

                //mStorage = FirebaseStorage.getInstance().getReference().child("Ingredients");
                uploadFile(ingredientName);
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


    private void uploadFile (String ingredientName) {
        if (selectedImage != null) {
            uploadPath = mStorage.child(user.getUid()).child(uid);
            Log.i(TAG, uploadPath.toString());
            uploadPath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressU.setVisibility(View.GONE);
                    Toast.makeText(PersonalIngredient.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();

                }
            });

            mIngredients.child(ingredientName).child("Image").setValue(uploadPath.toString());
//            uploadPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    String url = uri.toString();
//                    mIngredients.child(ingredientName).child("Image").setValue(url);
//                    Toast.makeText(PersonalIngredient.this, "URL Saved successfully", Toast.LENGTH_LONG).show();
//                }
//            });
        }
    }
}

