package com.example.vincentzhu.testapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    private ProgressBar progressR;

    private FirebaseAuth mAuth;

    protected void onCreate (Bundle SavedInstanceState) {

        super.onCreate (SavedInstanceState);
        setContentView(R.layout.login);

        buttonRegister=(Button) findViewById(R.id.buttonRegister);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        progressR=(ProgressBar) findViewById(R.id.progressReg);
        progressR.setVisibility(View.GONE);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),Introduction.class));
        }
    }

    private void registerUser () {
        progressR.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            //Email is empty
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
            //Stopping the function from executing
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //Password is empty
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
                    Toast.makeText(login.this, "Registered successfully, please verify your Email to login", Toast.LENGTH_LONG).show();
                    user.sendEmailVerification().addOnCompleteListener(login.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressR.setVisibility(View.GONE);
                                Toast.makeText(login.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                progressR.setVisibility(View.GONE);
                                Toast.makeText(login.this, "Failed to send verification email.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(login.this,LoginActivity.class));
                }else{
                    progressR.setVisibility(View.GONE);
                    Toast.makeText(login.this, "Account already exists or Email is incorrect, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClick(View view) {
        if (view == buttonRegister){
            registerUser();
        }
        if (view == textViewSignin){
            //Will open login activity here
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}
