package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterClinicDatActivity extends AppCompatActivity {
    String dni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_clinic_dat);
        //INTENT
        Intent intent = getIntent();
        dni = intent.getExtras().getString("dni");
        Button btn = findViewById(R.id.btnNext);
        Spinner spinSangre= findViewById(R.id.spinSangre);
        Spinner spinCancer= findViewById(R.id.spinCancer);
        EditText edtPeso = findViewById(R.id.edtPeso);
        EditText edtAltura = findViewById(R.id.edtAltura);
        //SPINNER;
        //Datos del spinner SAngre
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sangr, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinSangre.setAdapter(adapter);
        //Datos del spinner SAngre
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.cancer, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinCancer.setAdapter(adapter2);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //GUARDAR
                DocumentReference pacRef = db.collection("pacientes").document(dni);
                pacRef
                        .update(" altura",edtAltura.getText().toString(),
                                " peso",edtPeso.getText().toString(),"sangre",spinSangre.getSelectedItem().toString(),
                                "familiaCancer",spinCancer.getSelectedItem().toString()
                        );
                Intent HomeIntent= new Intent(getApplicationContext(),PatientActivity.class);
                startActivity(HomeIntent);
            }
        });
    }
}
