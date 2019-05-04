package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RegisterDoctorActivity extends AppCompatActivity {
    private TextView texto;
    private EditText Password;
    private ProgressBar pb;
    private String correo;
    private EditText rPassword;
    private Button enviar;
    private EditText username;
    private FirebaseAuth mAuth;//
    private boolean Coinciden(String password1, String password2){

        if (password1.equals(password2))
        {
            return true;
        }
        else
            return false;
    }
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length()>1) {
                //Ultimo caracter
                // texto2.setText(charSequence.subSequence(charSequence.length() - 1, charSequence.length()));

            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0)
            {
                texto.setText("Muy corta");
                pb.setProgress(0);
            }
            else if(editable.length()<6)
            {
                texto.setText("Muy Debil");
                pb.setProgress(25);
            }

            else if(editable.length()<10)
            {
                texto.setText("Debil");
                pb.setProgress(50);
            }

            else if(editable.length()<15)
            {
                texto.setText("Buena");
                pb.setProgress(75);
            }
            else
            {
                texto.setText("Excelente");
                pb.setProgress(100);
            }

            if(editable.length()>=20)
            {
                texto.setText("Muy larga");
                pb.setProgress(0);
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Password = (EditText) (findViewById(R.id.contraseña));
        pb= (ProgressBar)(findViewById(R.id.robustez));
        texto= (TextView)(findViewById(R.id.progress));
        Password.addTextChangedListener(watcher);
        enviar = (Button)(findViewById(R.id.enviar));
        rPassword= (EditText)(findViewById(R.id.contraseña2));
        username=(EditText)(findViewById(R.id.usuario));
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pb.getProgress()<25)
                    Password.setError("Contraseña debil");
                else {

                    if (Coinciden(Password.getText().toString(), rPassword.getText().toString()) == false) {
                        rPassword.setError("No coiniciden las contraseñas");
                    } else {

                        mAuth.createUserWithEmailAndPassword(username.getText().toString(), rPassword.getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent HomeIntent= new Intent(getApplicationContext(),RegisterDoctorInfoActivity.class);
                                    HomeIntent.putExtra("correo",username.getText().toString());
                                    startActivity(HomeIntent);
                                } else {
                                    // If sign in fails, display a message to the user.

                                }
                            }
                        });




                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
