package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.util.Sleeper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yashsoni.visualrecognitionsample.Clases.Paciente;
import com.yashsoni.visualrecognitionsample.adapters.PacienteAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PatientResumeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String dniPaciente;
    String correo;
    ArrayList<Paciente> patients;
    TextView txtDNI;
    TextView txtNombre;
    TextView txtApePat;
    TextView txtPorcentaje;
    TextView txtDiagnostico;
    TextView txtTratamiento;
    ImageView imgMelanoma;
    File localFile;
    String urlFoto;
    String filepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_resume);
        txtDNI=findViewById(R.id.txtDNI);
        txtNombre=findViewById(R.id.txtNomb);
        txtApePat=findViewById(R.id.txtApePat);
        txtPorcentaje=findViewById(R.id.txtPorc);
        txtDiagnostico=findViewById(R.id.txtDiagnostico);
        txtTratamiento=findViewById(R.id.txtTratamiento);
        imgMelanoma=findViewById(R.id.imgMelanoma);
        Intent intent = getIntent();
        dniPaciente=intent.getStringExtra("DNI");
        Log.d("dnipaciente",dniPaciente);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("pacientes").document(dniPaciente);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d("a", "DocumentSnapshot data: " + document.getData());
                        urlFoto=document.getData().get("url").toString();

                        txtDNI.setText(document.getData().get("dni").toString());
                        txtNombre.setText(document.getData().get("nombre").toString());
                        txtApePat.setText(document.getData().get("apPat").toString());
                        txtPorcentaje.setText(document.getData().get("Melanoma").toString());
                        txtDiagnostico.setText(document.getData().get("diagnostico").toString());
                        txtTratamiento.setText(document.getData().get("tratamiento").toString());

                    } else {
                        Log.d("a", "No such document");
                    }

                } else {
                    Log.d("a", "get failed with ", task.getException());
                }
            }
        });

        try {
            Thread.sleep(1000);
            if (urlFoto!=null){
            DownloadImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        imgMelanoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (urlFoto!=null) {
                        DownloadImage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void DownloadImage() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(urlFoto);

        localFile = File.createTempFile("images", "jpg");
        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("AR","SE CREO ARCHIVO");
                filepath=localFile.getAbsolutePath();
                File imgFile = new  File(filepath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgMelanoma.setImageBitmap(myBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("","faill");
            }
        });
    }
}
