package com.example.tfg_1.Armario;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class EtiquetarPrenda extends AppCompatActivity {
    String username, theme, editName;
    int categoria,idaux;
    Button back,home,calendar,aceptar;
    Uri imagenUri;
    ImageView imag;
    String tipo;
    ArrayList<String> colores;
    ArrayList<String> uso;
    ProgressBar uploading;
    TextView nombrePrenda, tipoTV, coloresTV, usoTV;
    BaseDatosPrendas DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etiquetarprenda);
        Intent intent=getIntent();
        if(intent!=null && intent.hasExtra("imagen")){
            username=intent.getStringExtra("username");
            imagenUri=intent.getParcelableExtra("imagen");
            tipo=intent.getStringExtra("tipo");
            colores=intent.getStringArrayListExtra("colores");
            uso=intent.getStringArrayListExtra("uso");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            editName=intent.getStringExtra("Name/Update");
        }
        DB=new BaseDatosPrendas(this);
        imag=findViewById(R.id.imagePrenda);

        nombrePrenda=findViewById(R.id.textNombPrenda);
        uploading=findViewById(R.id.progressBar);
        uploading.setVisibility(View.INVISIBLE);
        if(!editName.equals("")) {
            nombrePrenda.setText(editName);
            idaux=DB.darId(editName,username);
            //añadir imagen
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("prendas/" + username + "/" + idaux);
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imag.setImageBitmap(bm);
                }
            });
        }
        else
            imag.setImageURI(imagenUri);
        tipoTV=findViewById(R.id.editTextTipo);
        tipoTV.setText(tipo);

        coloresTV=findViewById(R.id.editTextColores);
        String c="";
        for (int i=0;i<colores.size();i++) {
            c=c+colores.get(i);
            if(i!=colores.size()-1)
                c=c+", ";
        }
        coloresTV.setText(c);

        usoTV=findViewById(R.id.editTextUso);
        String u="";
        for (int i=0;i<uso.size();i++) {
            u=u+uso.get(i);
            if(i!=uso.size()-1)
                u=u+", ";
        }
        usoTV.setText(u);



        aceptar=findViewById(R.id.bttnAcept);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(nombrePrenda.getText()))
                    Toast.makeText(EtiquetarPrenda.this,"Introduce un nombre a la prenda",Toast.LENGTH_LONG).show();
                else if(!editName.equals("")){
                    //estamos editando, por lo que solo haremos un update en bbdd
                    aceptar.setEnabled(false);
                    String np=nombrePrenda.getText().toString();
                    DB.editar(idaux,username,tipo,np,colores,uso);
                    Intent intent = new Intent(getApplicationContext(), InfoPrenda.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id",idaux);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
                    startActivity(intent);

                }
                else{
                    aceptar.setEnabled(false);
                    //pone datos en tabla de datos y en nube las imagenes
                    String np=nombrePrenda.getText().toString();
                    int id=DB.introducirPrenda(username, tipo, np, colores, uso);
                    if(id!=-1){
                        FirebaseStorage storage= FirebaseStorage.getInstance();
                        StorageReference storageRef=storage.getReference();
                        StorageReference userRef=storageRef.child("prendas/"+username);
                        StorageReference idRef=userRef.child(String.valueOf(id));
                        byte[] data;
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
                            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            data = baos.toByteArray();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        UploadTask uploadTask= idRef.putBytes(data);
                        uploading.setVisibility(View.VISIBLE);
                        uploadTask.addOnCompleteListener(EtiquetarPrenda.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                uploading.setVisibility(View.GONE);
                                aceptar.setEnabled(true);
                                Intent intent = new Intent(getApplicationContext(), PrendaClasificada.class);
                                intent.putExtra("username",username);
                                intent.putExtra("Theme", theme);
                                intent.putExtra("Categoria", categoria);
                                startActivity(intent);
                            }
                        });
                    }
                    else
                        Toast.makeText(EtiquetarPrenda.this,"Problema en la Base de Datos", Toast.LENGTH_SHORT);

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
}
