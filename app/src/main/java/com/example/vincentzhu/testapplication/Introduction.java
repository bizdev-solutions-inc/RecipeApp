package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class Introduction extends BaseActivity implements View.OnClickListener {

    private TextView tv_user_email;
    private static final int RESULT_IMAGE = 1;

    private ImageView imageDisplay;
    private FirebaseUser user;
    private StorageReference gsReference;
    private DatabaseReference mRoot;
    private String gs;
    private String userID;


    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_introduction);
        super.onCreate(savedInstanceState);

        String mainAccount = "devbizrecipe@gmail.com";

        FirebaseUser user= firebaseAuth.getCurrentUser();

        if(user.getEmail().equals(mainAccount)){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),Admin.class));
        }

        tv_user_email = (TextView) findViewById(R.id.tv_user_email);
        tv_user_email.setText("Welcome, "+user.getEmail());

        Button btn_continue = (Button)findViewById(R.id.btn_continue); //type cast

        //..set what happens when the user clicks
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Introduction.this, Home.class));
            }
        });

        userID  = firebaseAuth.getCurrentUser().getUid();
        imageDisplay = (ImageView)findViewById(R.id.imageDisplay);

        /**
         * Code below retrieves a gs URL from the database, and sends the URL to the storage to retrieve and display an image.
         *
         *
         */
//        mRoot = FirebaseDatabase.getInstance().getReference().child(userID).child("Added Ingredients").child("Ingredients").child("Green Onion").child("Image");
//        mRoot.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                gs = dataSnapshot.getValue().toString();
//                Log.i("Introduction", gs);
//                gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(gs);
//                Log.i("Introduction", gsReference.toString());
//                Glide.with(Introduction.this).using(new FirebaseImageLoader()).load(gsReference).into(imageDisplay);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void onClick(View view) {

    }
}
