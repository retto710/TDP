package com.yashsoni.visualrecognitionsample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import com.yashsoni.visualrecognitionsample.R;
import com.yashsoni.visualrecognitionsample.models.VisualRecognitionResponseModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    // IBM WATSON VISUAL RECOGNITION RELATED
    private final String API_KEY = "xi9bW2zISQPjPV4oqmVutxkbzVkuyac0oVnVtV_7sUJM";

    Button btnFetchResults;
    EditText etUrl;
    ProgressBar progressBar;
    FirebaseFirestore db;
    View content;
    Single<ClassifiedImages> observable;
    private float threshold = (float) 0.6;
    File localFile;
    String urlFoto;
    EditText edtDni;
    Button btnBuscar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        initializeViews();
        Intent intent = getIntent();
        String dniPaciente=intent.getStringExtra("DNI");
        edtDni= findViewById(R.id.edtDNI);
        edtDni.setText(dniPaciente);
        btnBuscar= findViewById(R.id.btnBuscar);
        BuscarCliente(dniPaciente);

        observable = Single.create((SingleOnSubscribe<ClassifiedImages>) emitter -> {
            IamOptions options = new IamOptions.Builder()
                    .apiKey(API_KEY)
                    .build();
            DownloadImage();
            VisualRecognition visualRecognition = new VisualRecognition("2018-03-19", options);
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(localFile)
                    //.url(etUrl.getText().toString())
                    .threshold((float) 0.6)
                    .classifierIds(Arrays.asList("CancerApp_2004385449"))
                    .build();

            ClassifiedImages classifiedImages = visualRecognition.classify(classifyOptions).execute();
            emitter.onSuccess(classifiedImages);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void goToNext(String url, List<ClassResult> resultList) {
        progressBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        // Checking if image has a class named "explicit". If yes, then reject and show an error msg as a Toast
        for (ClassResult result : resultList) {
            if(result.getClassName().equals("explicit")) {
                Toast.makeText(this, "NOT ALLOWED TO UPLOAD EXPLICIT CONTENT", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(this, "IMAGE DOESN'T CONTAIN ANY EXPLICIT CONTENT. OK TO PROCEED!", Toast.LENGTH_LONG).show();

        // No Explicit content found, go ahead with processing results and moving to Results Activity
        ArrayList<VisualRecognitionResponseModel> classes = new ArrayList<>();
        for (ClassResult result : resultList) {
            VisualRecognitionResponseModel model = new VisualRecognitionResponseModel();
            model.setClassName(result.getClassName());
            model.setScore(result.getScore());
            DocumentReference washingtonRef = db.collection("pacientes").document(edtDni.getText().toString());
            washingtonRef.update("Benigno","");
            washingtonRef.update("Maligno","");
            washingtonRef.update(result.getClassName(),result.getScore()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(" SUCCESS UPDATE", "DocumentSnapshot successfully updated!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(" FAIL UPDATE", "Error updating document", e);
                        }
                    });
            classes.add(model);
        }




        Intent i = new Intent(HomeActivity.this, ResultsActivity.class);
        i.putExtra("url", localFile.getAbsolutePath());
        i.putParcelableArrayListExtra("classes", classes);
        i.putExtra("dni",edtDni.getText().toString());
        startActivity(i);
    }

    private void initializeViews() {
        etUrl = findViewById(R.id.et_url);
        btnFetchResults = findViewById(R.id.btn_fetch_results);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        content = findViewById(R.id.ll_content);

        btnFetchResults.setOnClickListener(v -> {
            if (etUrl.getText().toString().endsWith(".png") || etUrl.getText().toString().endsWith(".jpg") || etUrl.getText().toString().endsWith(".jpeg")) {
                content.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                observable.subscribe(new SingleObserver<ClassifiedImages>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ClassifiedImages classifiedImages) {
                        System.out.println(classifiedImages.toString());
                        List<ClassResult> resultList = classifiedImages.getImages().get(0).getClassifiers().get(0).getClasses();
                        String url = classifiedImages.getImages().get(0).getSourceUrl();
                        goToNext(url, resultList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }
                });
            } else {
                Toast.makeText(HomeActivity.this, "Please make sure image URL is proper!", Toast.LENGTH_SHORT).show();
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
                        Log.d("resultado", "DocumentSnapshot data: " + document.getData());
                        urlFoto=document.getData().get("url").toString();
                    } else {
                        Log.d("resultado", "No such document");

                    }
                } else {
                    Log.d("resultado", "get failed with ", task.getException());
                }
            }
        });
    }

    private void DownloadImage() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference httpsReference = storage.getReferenceFromUrl(urlFoto);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Log.d("","1suc");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("","1fail");
            }
        });

        localFile = File.createTempFile("images", "jpg");
        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("AR","SE CREO ARCHIVO");
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