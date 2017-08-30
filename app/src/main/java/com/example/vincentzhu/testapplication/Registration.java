package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private ImageButton buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    private ProgressBar progressR;


    private FirebaseAuth mAuth;

    protected void onCreate (Bundle SavedInstanceState) {
        super.onCreate (SavedInstanceState);
        setContentView(R.layout.activity_registration);

        buttonRegister=(ImageButton) findViewById(R.id.buttonRegister);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        progressR=(ProgressBar) findViewById(R.id.progressReg);
        progressR.setVisibility(View.GONE);

       Window window = this.getWindow();
       window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
       window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null  && mAuth.getCurrentUser().isEmailVerified()){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),Home.class));
        }
    }

    private void registerUser () {
        progressR.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            //Email is empty
            progressR.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //Password is empty
            progressR.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;
        }

        //if validations are ok
        //we will first show a progress bar
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (task.isSuccessful()) {
                    //User is successfully registered
                    progressR.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Registered successfully, please verify your Email", Toast.LENGTH_LONG).show();
                    user.sendEmailVerification().addOnCompleteListener(Registration.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressR.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, "Verification Email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                progressR.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, "Failed to send Verification Email.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(Registration.this,LoginActivity.class));
                }else{
                    progressR.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Account already exists or Email is invalid. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClick(View view) {
        if (view == buttonRegister){
            registerUser();
            finish();
        }
        if (view == textViewSignin){
            //Will open activity_registration activity here
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
