package com.yashsoni.visualrecognitionsample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        EditText edtCorreo = findViewById(R.id.txt_forgotemail);
        String emailAddress = edtCorreo.getText().toString();
        Button continuar = findViewById(R.id.continuar);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("", "Email sent.");
                                }
                            }
                        });
            }
        });


    }
}
