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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Integer flgPaciente;
    EditText edtNombre;
    EditText edtApellidoPat;
    EditText edtApellidoMat;
    EditText edtDNI;
    EditText edtEdad;
    Spinner spinGenero;
    EditText edtDireccion;
    EditText edtTelefono;
    EditText edtCorreo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        flgPaciente=0;
        //Iniciar variables
        edtNombre=findViewById(R.id.edtNombre);
        edtApellidoPat=findViewById(R.id.edtApellidoPaterno);
        edtApellidoMat=findViewById(R.id.edtApellidoMaterno);
        edtDNI=findViewById(R.id.edtDNI);
        edtEdad=findViewById(R.id.edtEdad);
        spinGenero=findViewById(R.id.spinGenero);
        edtDireccion=findViewById(R.id.edtDireccion);
        edtTelefono=findViewById(R.id.edtTelefono);
        edtCorreo=findViewById(R.id.edtCorreo);
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
                CrearPaciente();
                Intent HomeIntent= new Intent(getApplicationContext(),MenuAuxActivity.class);
                startActivity(HomeIntent);
            }
        });


    }

    private void CrearPaciente()
    {


        BuscarCliente(edtDNI.getText().toString());
        if (flgPaciente==0){
        CollectionReference pacientes = db.collection("pacientes");
        Map<String, Object> paciente = new HashMap<>();
        paciente.put("nombre", edtNombre.getText().toString());
        paciente.put("apPat", edtApellidoPat.getText().toString());
        paciente.put("apMat", edtApellidoMat.getText().toString());
        paciente.put("dni", edtDNI.getText().toString());
        paciente.put("tlf", edtTelefono.getText().toString());
        paciente.put("correo", edtCorreo.getText().toString());
        paciente.put("edad", edtEdad.getText().toString());
        paciente.put("gen", spinGenero.getSelectedItem().toString());
        paciente.put("dir", edtDireccion.getText().toString());
        paciente.put("url", "");
        paciente.put("derm", "");
        paciente.put("clase", "");
        paciente.put("score", "");
        // Add a new document with a generated ID
        /*
        db.collection("pacientes")
                .add(paciente)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("D", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("D", "DocumentSnapshot added with ID: " + e);
                    }
                });*/
        //Otra manera de guardar
        pacientes.document(edtDNI.getText().toString()).set(paciente);
        BuscarCliente(edtDNI.getText().toString());
        }
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
                        Log.d("resultado", "DocumentSnapshot data: " + document.getData());
                        flgPaciente=1;
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
