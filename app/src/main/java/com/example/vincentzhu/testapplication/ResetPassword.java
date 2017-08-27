package com.example.vincentzhu.testapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.vincentzhu.testapplication.R.id.buttonRegister;
import static com.example.vincentzhu.testapplication.R.id.textViewSignin;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{

    private String email;
    private EditText editResetEmail;
    private TextView loginPage;
    private ImageButton passReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        editResetEmail = findViewById(R.id.editResetEmail);
        loginPage = (TextView) findViewById(R.id.textViewReset);
        passReset = (ImageButton) findViewById(R.id.buttonResetPass);

        loginPage.setOnClickListener(this);
        passReset.setOnClickListener(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

    }

    public void resetPassWD (){
        email = editResetEmail.getText().toString();

        if (!email.isEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPassword.this, "Reset password email sent.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ResetPassword.this, "Invalid email!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(ResetPassword.this, "Please enter a valid email.", Toast.LENGTH_LONG).show();
        }


    }

    public void onClick(View view){
        if (view == passReset){
            resetPassWD();
        }
        if (view == loginPage){
            //Will open login activity here
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

}

