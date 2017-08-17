package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.vincentzhu.testapplication.R.id.pic_btn;

public class PersonalRecipe extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private Button mPicture;
    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private EditText mNameField;
    private EditText mIngField;
    private EditText mInstrField;

    private DatabaseReference mName;
    private DatabaseReference mIng;
    private DatabaseReference mInstr;
    private DatabaseReference mCuisine;
    private DatabaseReference mMealType;
    private StorageReference mStorage;
    private Uri selectedImage;
    private ProgressBar progressU;


    private String userID;
    private ImageView imageDisplay;
    private static final int RESULT_IMAGE = 1;

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

        // Get a support ActionBar corresponding to this toolbar and enable Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPicture = (Button) findViewById(pic_btn);
        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        imageDisplay = (ImageView) findViewById(R.id.imageDisplay);

        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(userID).child("Recipe");

        mNameField = (EditText)findViewById(R.id.name_field);
        mIngField = (EditText)findViewById(R.id.ing_field);
        mInstrField = (EditText)findViewById(R.id.instr_field);

        mStorage = FirebaseStorage.getInstance().getReference();
        mPicture.setOnClickListener(this);
        mFirebaseBtn.setOnClickListener(this);
        progressU=(ProgressBar) findViewById(R.id.progressUpload);
        progressU.setVisibility(View.GONE);

//               mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                String name = mNameField.getText().toString().trim();
//                String ing = mIngField.getText().toString().trim();
//                String instr = mInstrField.getText().toString().trim()
        //        String key = mDatabase.push().getKey();
                //mDatabase.setValue(name);
//                mRef = mDatabase.child(key);
//                mName = mRef.child("name");
//                mIng = mRef.child("ingredients");
                //mCuisine = mRef.child("cuisine");
                //mMealType = mRef.child("meal type");
//                mInstr = mRef.child("instructions");
//                mName.setValue(name);
//                mIng.setValue(ing);
//                mInstr.setValue(instr);
//            }
//        });

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
//            case R.id.action_search:
//                // User chose the "Search" item, show search dialog
//                onSearchRequested();
//                return true;
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
            case pic_btn:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_IMAGE);
                break;
            case R.id.firebase_btn:
                String name = mNameField.getText().toString().trim();
                String ing = mIngField.getText().toString().trim();
                String instr = mInstrField.getText().toString().trim();
                String key = mDatabase.push().getKey();
                mDatabase.setValue(name);
                mRef = mDatabase.child(key);
                mName = mRef.child("name");
                mIng = mRef.child("ingredients");
                mInstr = mRef.child("instructions");
                mName.setValue(name);
                mIng.setValue(ing);
                mInstr.setValue(instr);
                uploadFile();
                Toast.makeText(PersonalRecipe.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();
//                FirebaseUser user= firebaseAuth.getCurrentUser();
//                StorageReference uploadPath = mStorage.child(user.getEmail()).child(selectedImage.getLastPathSegment());
//                uploadPath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(PersonalRecipe.this,"Upload Completed successfully",Toast.LENGTH_LONG).show();
//                    }
//                });

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

    private void uploadFile () {
        if (selectedImage != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            StorageReference uploadPath = mStorage.child(user.getEmail()).child(selectedImage.getLastPathSegment());
            uploadPath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressU.setVisibility(View.GONE);
//                    Toast.makeText(PersonalRecipe.this, "Upload Completed successfully", Toast.LENGTH_LONG).show();

                }
            });

        }

    }
}

