package com.example.tfg_1.Armario;


import android.Manifest;
import android.annotation.SuppressLint;
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

public class GaleryActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GALERY = 1234;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    String username, theme;
    int categoria, id,count;
    ImageView imag;
    Uri imagenUri;
    Button redo, acept;
    Button back,home,calendar;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeriaactivity);

        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            id=intent.getIntExtra("id",0);
        }

        redo=findViewById(R.id.buttonRedo4);
        acept=findViewById(R.id.buttonAcept2);
        openGalery();

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalery();

            }
        });
        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id!=0)
                {
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
    private void openGalery(){
        Intent gallery= new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,REQUEST_CODE_GALERY);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_GALERY && resultCode== RESULT_OK) {
            imagenUri= data.getData();
            imag=findViewById(R.id.imagenCamara3);
            imag.setImageURI(imagenUri);
        }
    }
    private void aniadirFotoOutfit() throws FileNotFoundException {
        //para tema outfits
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String n=imagenUri.toString().substring(imagenUri.toString().length() - 10);
        if(!n.startsWith("/g"))
            n="/g"+n;
        StorageReference islandRef = storageRef.child("outfits/" + username + "/fotos_reales/" + id + n);
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
}
