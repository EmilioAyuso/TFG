package com.example.tfg_1.Armario;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PrendaClasificada extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1234;
    String username, theme;
    TextView name;
    Button back,home,calendar,aniadir;
    int categoria, faltan;
    ProgressBar progressBar;
    GridView fotos;
    BaseDatosPrendas BD;
    List<Integer> idFotos, listaOrdenid;
    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prenda_clasificada);
        BD=new BaseDatosPrendas(this);
        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
        }
        name=findViewById(R.id.txttheme);
        name.setText(theme);
        //boton añadir
        aniadir=findViewById(R.id.buttonAñadir);
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BD.getUsoExtra().isEmpty()){
                    Toast.makeText(PrendaClasificada.this, "Debes de tener algún Uso guardado", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder elegir = new AlertDialog.Builder(PrendaClasificada.this);
                    elegir.setCancelable(true).setPositiveButton("Galería", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(PrendaClasificada.this, GaleryActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("Theme", theme);
                            intent.putExtra("Categoria", categoria);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Cámara", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(PrendaClasificada.this, CameraActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("Theme", theme);
                            intent.putExtra("Categoria", categoria);
                            startActivity(intent);
                        }
                    });
                    AlertDialog titulo = elegir.create();
                    titulo.setTitle("Añadir prenda desde:");
                    titulo.show();
                }
            }
        });
        //actualizar fotos
        fotos=findViewById(R.id.listaRopa);
        List<byte[]> listafotos=new ArrayList<>();
        idFotos=null;
        listaOrdenid=new ArrayList<>();
        if(categoria==1){
            idFotos=BD.pedirTodasFotosTipo(theme);
        } else if (categoria==2) {
            idFotos=BD.pedirTodasFotosColor(theme);
        } else if(categoria==3) {
            idFotos = BD.pedirTodasFotosUso(theme);
        }

        progressBar=findViewById(R.id.progressBar4);
        if(idFotos==null || idFotos.isEmpty())
            progressBar.setVisibility(View.INVISIBLE);

        else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            faltan=idFotos.size();
            progressBar.setVisibility(View.VISIBLE);
            for (int id : idFotos) {
                StorageReference storageRef = storage.getReference();
                StorageReference islandRef=storageRef.child("prendas/"+username+"/"+id);
                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        listafotos.add(bytes);
                        listaOrdenid.add(id);
                        PrendaAdapter adapter= new PrendaAdapter(PrendaClasificada.this,listafotos);
                        fotos.setAdapter(adapter);
                        faltan--;
                        if(faltan==0)
                            progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            PrendaAdapter adapter= new PrendaAdapter(this,listafotos);
            fotos.setAdapter(adapter);
        }

        //tocar una foto
        fotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id= listaOrdenid.get(i);
                Intent intent=new Intent(getApplicationContext(),InfoPrenda.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
                startActivity(intent);
            }
        });




        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Prendas.class);
                intent.putExtra("username",username);
                startActivity(intent);
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
