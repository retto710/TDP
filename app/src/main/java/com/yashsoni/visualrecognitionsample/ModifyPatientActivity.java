package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyPatientActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Integer flgPaciente;
    EditText edtNombre;
    EditText edtApellidoPat;
    EditText edtApellidoMat;
    EditText edtDNI;
    String correo;
    EditText edtEdad;
    Spinner spinGenero;
    EditText edtDireccion;
    EditText edtTelefono;
    EditText edtCorreo;
    String dni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_patient);
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correo=currentUser.getEmail();
        flgPaciente=0;
        //Iniciar variables
        edtNombre=findViewById(R.id.edtNombre);
        edtApellidoPat=findViewById(R.id.edtApellidoPaterno);
        edtApellidoMat=findViewById(R.id.edtApellidoMaterno);
        edtDNI=findViewById(R.id.edtDNI);
        edtEdad=findViewById(R.id.edtEdad);
        spinGenero=findViewById(R.id.spinGenero);
        //edtDireccion=findViewById(R.id.edtDireccion);
        edtTelefono=findViewById(R.id.edtTelefono);
        edtCorreo=findViewById(R.id.edtCorreo);
        dni=edtCorreo.getText().toString();
        Intent intent = getIntent();
        dni = intent.getExtras().getString("DNI");
        Toast.makeText(this,dni,Toast.LENGTH_LONG);
        BuscarCliente(dni);
        //Datos del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinGenero.setAdapter(adapter);
        Button btnRegister = (Button) findViewById(R.id.btnGuardar);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference pacRef = db.collection("pacientes").document(dni);
                pacRef
                        .update("nombre", edtNombre.getText().toString(),
                                "apPat", edtApellidoPat.getText().toString(),
                                "apMat", edtApellidoMat.getText().toString(),
                                "tlf", edtTelefono.getText().toString(),
                                "correo", edtCorreo.getText().toString(),
                                "edad", edtEdad.getText().toString(),
                                "gen", spinGenero.getSelectedItem().toString()

                        );
                Intent HomeIntent= new Intent(getApplicationContext(),PatientActivity.class);
                HomeIntent.putExtra("dni",dni);
                startActivity(HomeIntent);

            }
        });
    }
    private void BuscarCliente(String dni)
    {
        DocumentReference docRef = db.collection("pacientes").document(dni);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       edtNombre.setText(document.getData().get("nombre").toString());
                        edtApellidoPat.setText(document.getData().get("apPat").toString());
                        edtApellidoMat.setText(document.getData().get("apMat").toString());
                        edtTelefono.setText(document.getData().get("tlf").toString());
                        edtCorreo.setText(document.getData().get("correo").toString());
                        edtEdad.setText(document.getData().get("edad").toString());
                        //edtDireccion.setText(document.getData().get("direccion").toString());
                    } else {
                        Log.d("resultado", "No such document");
                        flgPaciente=0;
                    }
                } else {
                    Log.d("resultado", "get failed with ", task.getException());
                }
            }
        });
    }
}
