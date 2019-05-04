package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.BitSet;

public class RegisterGeoDatActivity extends AppCompatActivity {
    ArrayList<String> departamentos = new ArrayList<String>();
    ArrayList<String> provincias = new ArrayList<String>();
    ArrayList<String> distritos = new ArrayList<String>();
    private  String strDepart;
    private String strProv;
    private String strDist;
    String dni;
    AutoCompleteTextView autoDepartamento;
    AutoCompleteTextView autoProvincia;
    AutoCompleteTextView autoDistrito;
    Spinner spinZona;
    EditText edtDireccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_geo_dat);
        //INTENT
        Intent intent = getIntent();
        dni = intent.getExtras().getString("dni");
        //INICIALIZAR
        Button btn = findViewById(R.id.btnNext);
        autoDepartamento = findViewById(R.id.autoDepart);
        autoProvincia = findViewById(R.id.autoProv);
        autoDistrito = findViewById(R.id.autoDist);
        spinZona= findViewById(R.id.spinZona);
        edtDireccion= findViewById(R.id.edtDireccion);
        autoDepartamento.setEnabled(false);
        autoProvincia.setEnabled(false);
        autoDistrito.setEnabled(false);
        strDepart="";
        strDist="";
        strProv="";
        //SPINNER
        //Datos del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.zonificacion, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinZona.setAdapter(adapter);

        //DEPARTAMENTOS
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        db.collection("departamentos").whereEqualTo("PROVINCIA", strProv)
                .whereEqualTo("DISTRITO", strDist).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                departamentos.add(document.getData().get("DEPARTAMENTO").toString());
                            }
                            autoDepartamento.setEnabled(true);
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
        //DEPARTAMENTOS
        autoDepartamento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               if (autoDistrito.isEnabled()) {
                   //autoDistrito.setText("");
                   //autoProvincia.setText("");
                   autoDistrito.setEnabled(false);
                   autoProvincia.setEnabled(false);
               }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Departamentos();
        //Cuando seleccionas
        autoDepartamento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strDepart=autoDepartamento.getText().toString();
                autoDistrito.setText("");
                autoProvincia.setText("");
                strDist="";
                provincias = new ArrayList<String>();
                db.collection("departamentos").whereEqualTo("DEPARTAMENTO", strDepart)
                        .whereEqualTo("DISTRITO", strDist).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("PROVINCIA").toString()!="") {
                                    Log.d("provincia",document.getData().get("PROVINCIA").toString());
                                    provincias.add(document.getData().get("PROVINCIA").toString());
                                }
                            }
                            autoProvincia.setEnabled(true);
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
                Provincias();
            }
        });
        //PROVINCIAS
        autoProvincia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (autoDistrito.isEnabled()) {
                    //autoProvincia.setText("");
                    autoDistrito.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strProv=autoProvincia.getText().toString();
                autoDistrito.setText("");
                distritos = new ArrayList<String>();
                db.collection("departamentos").whereEqualTo("DEPARTAMENTO", strDepart)
                        .whereEqualTo("PROVINCIA", strProv).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("DISTRITO").toString()!="") {
                                    distritos.add(document.getData().get("DISTRITO").toString());
                                }
                            }
                            autoDistrito.setEnabled(true);
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
                Distritos();
            }
        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GUARDAR DATOS
                DocumentReference pacRef = db.collection("pacientes").document(dni);
                pacRef
                        .update(" departamento",autoDepartamento.getText().toString(),
                                " provincia",autoProvincia.getText().toString(),"distrito",autoDistrito.getText().toString(),
                                "direccion",edtDireccion.getText().toString(),"zonificacion",spinZona.getSelectedItem().toString()
                                );
                Intent HomeIntent= new Intent(getApplicationContext(),RegisterClinicDatActivity.class);
                HomeIntent.putExtra("dni",dni);
                startActivity(HomeIntent);
            }
        });
    }

    private void Departamentos()
    {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, departamentos);
        autoDepartamento.setAdapter(adapter2);
    }
    private void Provincias()
    {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, provincias);
        autoProvincia.setAdapter(adapter2);
    }
    private void Distritos()
    {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, distritos);
        autoDistrito.setAdapter(adapter2);
    }
}
