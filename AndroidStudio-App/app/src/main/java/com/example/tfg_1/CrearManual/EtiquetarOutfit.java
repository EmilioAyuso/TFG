package com.example.tfg_1.CrearManual;

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

import com.example.tfg_1.Armario.EtiquetarPrenda;
import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
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

public class EtiquetarOutfit extends AppCompatActivity {

    String username, theme;
    final long ONE_MEGABYTE = 1024 * 1024;
    int categoria,id;
    Button back,home,calendar,aceptar;
    ImageView imag;
    ArrayList<String> colores;
    ArrayList<String> uso;
    ProgressBar uploading;
    TextView nombrePrenda, coloresTV, usoTV;
    BaseDatosOutfits DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etiquetaroutfit);
        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            id=intent.getIntExtra("id",0);
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            colores=intent.getStringArrayListExtra("colores");
            uso=intent.getStringArrayListExtra("uso");
        }
        DB=new BaseDatosOutfits(this);
        imag=findViewById(R.id.imagePrenda);

        nombrePrenda=findViewById(R.id.textNombPrenda);
        nombrePrenda.setText(DB.pedirNamePrenda(id));
        uploading=findViewById(R.id.progressBar);

        //añadimos imagen
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("outfits/" + username + "/" + id);
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imag.setImageBitmap(bm);
                uploading.setVisibility(View.GONE);
            }
        });

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
                    Toast.makeText(EtiquetarOutfit.this,"Introduce un nombre a la prenda",Toast.LENGTH_LONG).show();
                else{
                    aceptar.setEnabled(false);
                    //update de caracteristicas en la bbdd
                    DB.editarCaracteristicasOutfit(id,nombrePrenda.getText().toString(),colores,uso);
                    aceptar.setEnabled(true);
                    Intent intent = new Intent(getApplicationContext(), InfoOutfit.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id",id);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
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
}
