package com.example.vincentzhu.testapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{

    private String email;
    private EditText editResetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        editResetEmail = findViewById(R.id.editResetEmail);
    }

    public void onClick(View view)
    {
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
}
