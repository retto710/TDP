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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterDoctorInfoActivity extends AppCompatActivity {
    private String correo;
    FirebaseFirestore db;
    Integer flgdoctor;
    EditText edtNombre;
    EditText edtApellidoPat;
    EditText edtApellidoMat;
    EditText edtDNI;
    EditText edtEdad;
    Spinner spinGenero;
    EditText edtHospital;
    EditText edtTelefono;
    EditText edtCorreo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor_info);
        //INTENT
        Intent intent = getIntent();
        correo = intent.getExtras().getString("correo");
        db = FirebaseFirestore.getInstance();
        flgdoctor=0;
        //Iniciar variables
        edtNombre=findViewById(R.id.edtNombre);
        edtApellidoPat=findViewById(R.id.edtApellidoPaterno);
        edtApellidoMat=findViewById(R.id.edtApellidoMaterno);
        edtDNI=findViewById(R.id.edtDNI);
        edtEdad=findViewById(R.id.edtEdad);
        spinGenero=findViewById(R.id.spinGenero);
        edtHospital=findViewById(R.id.edtHospital);
        edtTelefono=findViewById(R.id.edtTelefono);
        edtCorreo=findViewById(R.id.edtCorreo);
        edtCorreo.setText(correo);
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
                CrearDoctor();
                Intent HomeIntent= new Intent(getApplicationContext(),PatientActivity.class);
                HomeIntent.putExtra("dni",edtDNI.getText().toString());
                startActivity(HomeIntent);
            }
        });
    }
    private void CrearDoctor()
    {

        BuscarDoctor(edtDNI.getText().toString());
        if (flgdoctor==0){
            CollectionReference doctores = db.collection("doctores");
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("nombre", edtNombre.getText().toString());
            doctor.put("apPat", edtApellidoPat.getText().toString());
            doctor.put("apMat", edtApellidoMat.getText().toString());
            doctor.put("dni", edtDNI.getText().toString());
            doctor.put("tlf", edtTelefono.getText().toString());
            doctor.put("correo", edtCorreo.getText().toString());
            doctor.put("edad", edtEdad.getText().toString());
            doctor.put("gen", spinGenero.getSelectedItem().toString());
            doctor.put("hospital", edtHospital.getText().toString());
            //doctor.put("dir", edtDireccion.getText().toString());
            // Add a new document with a generated ID
        /*
        db.collection("doctores")
                .add(doctor)
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

            doctores.document(edtCorreo.getText().toString()).set(doctor);
            BuscarDoctor(edtCorreo.getText().toString());
        }
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
                        Log.d("resultado", "DocumentSnapshot data: " + document.getData());
                        flgdoctor=1;
                    } else {
                        Log.d("resultado", "No such document");
                        flgdoctor=0;
                    }
                } else {
                    Log.d("resultado", "get failed with ", task.getException());
                }
            }
        });
    }
}
