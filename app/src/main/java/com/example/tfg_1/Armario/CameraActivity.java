package com.example.tfg_1.Armario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.CrearManual.InfoOutfit;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1234;
    private static final int CAPTURE_CODE = 1001;
    String username, theme;
    int categoria, id;
    ImageView imag;
    Button acept,redo;
    Button back,home,calendar;
    Uri imagenUri;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraactivity);

        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            id=intent.getIntExtra("id",0);
        }

        imag=findViewById(R.id.imagenCamara);
        acept=findViewById(R.id.buttonAcept);
        redo=findViewById(R.id.buttonRedo);

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            openCamera();
        }

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                } else {
                    openCamera();
                }
            }
        });
        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id!=0) {
                    try {
                        aniadirFotoOutfit();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    ArrayList<String> colores = new ArrayList<>();
                    ArrayList<String> usos = new ArrayList<>();
                    if (categoria == 2)
                        colores.add(theme);
                    if (categoria == 3)
                        usos.add(theme);

                    Intent intent = new Intent(getApplicationContext(), ListaOpciones.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
                    intent.putExtra("imagen", imagenUri);
                    intent.putExtra("Update", "");

                    intent.putStringArrayListExtra("ListaColores", colores);
                    intent.putStringArrayListExtra("ListaUsos", usos);
                    startActivity(intent);
                }
            }
        });

        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        home=findViewById(R.id.Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), PantallaInicio.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        calendar=findViewById(R.id.Calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), PantallaCalendario.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }

    private void aniadirFotoOutfit() throws FileNotFoundException {
        //para tema outfits
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String n=imagenUri.toString().substring(imagenUri.toString().length() - 10);
        if(!n.startsWith("/f"))
            n="/f"+n;
        StorageReference islandRef = storageRef.child("outfits/" + username + "/fotos_reales/" + id +n);
        InputStream inputStream = getContentResolver().openInputStream(imagenUri);
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        islandRef.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Intent intent=new Intent(getApplicationContext(), InfoOutfit.class);
                intent.putExtra("username",username);
                intent.putExtra("id",id);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
                startActivity(intent);
            }
        });

    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"FROM CAMERA");
        imagenUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
        startActivityForResult(intent,CAPTURE_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
                Toast.makeText(this, "permission accepted", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            imag.setImageURI(imagenUri);
        }

    }
}
