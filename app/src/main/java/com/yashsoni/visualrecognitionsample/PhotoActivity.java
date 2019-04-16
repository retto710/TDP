package com.yashsoni.visualrecognitionsample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity  {
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
    EditText edtDNI;
    Button btnBuscar;
    Button btnCamara;
    TextView txtNombre;
    TextView txtApePat;
    Spinner spinDerm;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    List<String> dermatologos = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        btnCamara = (Button) findViewById(R.id.btn1);
        db = FirebaseFirestore.getInstance();
        edtDNI= findViewById(R.id.edtDNI);
        btnBuscar= findViewById(R.id.btnBuscar);
        txtNombre= findViewById(R.id.txtNombre);
        txtApePat= findViewById(R.id.txtApePat);
        spinDerm= findViewById(R.id.spinDerm);



        db.collection("dermatologos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("", document.getId() + " => " + document.getData());
                                dermatologos.add(document.getData().get("nombre").toString());

                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //Datos del spinner
        dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        dermatologos);
        // Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinDerm.setAdapter(dataAdapter);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuscarCliente(edtDNI.getText().toString());
            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialogImage();
            }
        });


    }
    private void startDialogImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Fotografia Lunar");
        myAlertDialog.setMessage("Que desea abrir?");
        myAlertDialog.setPositiveButton("Galeria",
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
                FileProvider.getUriForFile(PhotoActivity.this, getApplicationContext().getPackageName()+ ".provider",f));

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
        String nombre =edtDNI.getText().toString()+".jpg";
        String nombre2 ="images/"+edtDNI.getText().toString()+".jpg";
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
                    DocumentReference washingtonRef = db.collection("pacientes").document(edtDNI.getText().toString());
                    washingtonRef
                            .update("url", downloadUri.toString()
                                    )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        btnCamara.setEnabled(true);
                        txtApePat.setText( document.getData().get("apPat").toString());
                        txtNombre.setText( document.getData().get("nombre").toString());
                    } else {
                        Log.d("resultado", "No such document");
                        btnCamara.setEnabled(false);
                        txtApePat.setText("");
                        txtNombre.setText("");
                    }
                } else {
                    Log.d("resultado", "get failed with ", task.getException());
                }
            }
        });
    }



}
