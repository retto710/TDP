package com.yashsoni.visualrecognitionsample;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.yashsoni.visualrecognitionsample.Clases.Paciente;
import com.yashsoni.visualrecognitionsample.activities.HomeActivity;
import com.yashsoni.visualrecognitionsample.activities.ResultsActivity;
import com.yashsoni.visualrecognitionsample.adapters.PacienteAdapter;
import com.yashsoni.visualrecognitionsample.models.VisualRecognitionResponseModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PatientActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    protected static final int GALLERY_PICTURE = 1;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int MY_CAMERA_REQUEST_CODE=100;
    private String txt_image_path;
    Bitmap bitmap;
    String selectedImagePath;
    private Uri downloadUrl;
    FirebaseFirestore db;
    String dniPaciente;
    String correo;
    ArrayList<Paciente> patients;
    LinearLayout rootView;
    TextView patientView;
    PacienteAdapter itemsAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.tlbPlus)
        {
            Intent HomeIntent= new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(HomeIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    ListView listView;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        db = FirebaseFirestore.getInstance();
        patients= new ArrayList<Paciente>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correo=currentUser.getEmail();
        listView = (ListView) findViewById(R.id.list);
        db.collection("pacientes").whereEqualTo("doctor",correo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String dni=document.getData().get("dni").toString();
                        String apePat=document.getData().get("apPat").toString();
                        String photoUrl="";
                        if (document.getData().get("url").toString().isEmpty())
                            photoUrl=document.getData().get("url").toString();
                        String score="";
                        //if (document.getData().get("score").toString().isEmpty())
                        //score=document.getData().get("score").toString();

                        Paciente paciente = new Paciente(apePat,dni,photoUrl,score);
                        patients.add(paciente);
                    }
                    //itemsAdapter= new ArrayAdapter<Paciente>(getApplicationContext(), R.layout.list_item, patients);
                    itemsAdapter= new PacienteAdapter(getApplicationContext(),patients);
                    //listView = (ListView) findViewById(R.id.list);

                    listView.setAdapter(itemsAdapter);



                } else {
                    Log.d("", "Error getting documents: ", task.getException());
                }
            }
        });
         patientView = new TextView(getApplicationContext());
        // Create a reference to the patients collection

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paciente paciente= patients.get(position);
                dniPaciente=paciente.getDni();
                startDialogImage();
                Log.d("pacientesize",String.valueOf(patients.size()));
                Log.d("pacientesize",patients.get(0).getDni());
                Log.d("pacientedni", paciente.getDni());
                Log.d("posicion", String.valueOf(position));
            }
        });



    }
    private void startDialogImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Fotografia Lunar");
        myAlertDialog.setMessage("Que desea abrir?");
        myAlertDialog.setPositiveButton("Ver Diagnostico",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;
                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);
                    }
                });
        myAlertDialog.setNegativeButton("Camara",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        CameraPermission();
                    }
                });
        myAlertDialog.show();
    }

    private void CameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else{
            openCamera();
        }
    }

    private void openCamera(){
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(PatientActivity.this, getApplicationContext().getPackageName()+ ".provider",f));

        startActivityForResult(intent,
                CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        selectedImagePath = null;
        //Si se selecciono camara
        Log.d(" RESULT"," RESULTADO");
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST){
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }

            }
            if (!f.exists()) {
                Toast.makeText(getBaseContext(),
                        "Error while capturing image", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                selectedImagePath=getRealPathFromURI(tempUri);
                txt_image_path=selectedImagePath;
                Log.d("path",selectedImagePath);
                UploadImage();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        //Si fue galeria
        else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE)
        {
            Log.d(" GALERIA"," INGRESO GALERIA");
            if (data != null) {
                Log.d(" GALERIA"," DATA NO ES NULL");
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
                    txt_image_path=selectedImagePath;
                    Log.d("path",selectedImagePath);
                }
                UploadImage();
                Log.d(" GALERIA"," DATA NULL");
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void UploadImage(){
        Log.d(" GALERIA"," SUBIR IMAGEN");
        FirebaseApp customApp = FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        String nombre =dniPaciente;
        String nombre2 ="images/"+dniPaciente+".jpg";
        StorageReference mountainsRef = storageRef.child(nombre);
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        Log.d(" GALERIA"," TERMINO REFERENCIAS");
        Uri file = Uri.fromFile(new File(txt_image_path));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask;
        uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        Log.d(" paso 1"," termin ode subir la foto");


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    DocumentReference washingtonRef = db.collection("pacientes").document(dniPaciente);
                    washingtonRef
                            .update("url", downloadUri.toString()
                            )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(" SUCCESS UPDATE", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(getBaseContext(),
                                            "Imagen Subida", Toast.LENGTH_LONG)
                                            .show();
                                    Intent HomeIntent= new Intent(getApplicationContext(),HomeActivity.class);
                                    HomeIntent.putExtra("DNI",dniPaciente);
                                    startActivity(HomeIntent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(" FAIL UPDATE", "Error updating document", e);
                                }
                            });
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        Log.d(" paso 2"," tengo el url");

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}
