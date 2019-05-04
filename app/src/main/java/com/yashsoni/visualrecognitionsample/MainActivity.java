package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashsoni.visualrecognitionsample.Clases.Paciente;
import com.yashsoni.visualrecognitionsample.adapters.PacienteAdapter;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String dni;
    Intent intent;
    @Override
    protected void onStart() {
        super.onStart();

        //Si esta iniciada la sesion
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Variables
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Button btnLogIn = findViewById(R.id.btn_login);
        TextView txtRegister = findViewById(R.id.txt_register);
        TextView txtForgotPassword = findViewById(R.id.txt_forgotpassword);
        //Funciones click
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),RegisterDoctorActivity.class);
                startActivity(intent);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextEmail = findViewById(R.id.email);
                EditText editTextContra = findViewById(R.id.password);
                if (validation(editTextContra.getText().toString(),editTextEmail.getText().toString()))
                    Login(editTextEmail.getText().toString(),editTextContra.getText().toString());
                else
                {
                    AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Campos vacios. Ingrese correo y/o contrasena");
                    alert.setTitle("Alerta");
                    AlertDialog dialog =alert.create();
                    dialog.show();
                }
            }
        });
    }
    private boolean validation(String a, String b)
    {
        if (a.isEmpty()||b.isEmpty())
        {
            return false;
        }
        return true;
    }


    private void Login(String email, String password) {
        Log.d("login", "signIn:" + email);
        //if (!validateForm()) {
        //    return;
        //}
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("d", "signInWithEmail:success");
                            Log.d("correodoctor", email);
                            FirebaseUser user = mAuth.getCurrentUser();
                            intent = new Intent(getApplicationContext(), PatientActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                            //BuscarDoctor(email);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("d", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }
                    }
                });
        // [END sign_in_with_email]
    }
    private void BuscarDoctor(String dni)
    {
        DocumentReference docRef = db.collection("doctores").document(dni);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String dni=document.getData().get("dni").toString();
                        intent.putExtra(" dni",dni);
                        startActivity(intent);
                    }
                } else {
                    Log.d("resultado", "get failed with ", task.getException());
                }
            }
        });
    }
}
