package com.example.tfg_1.CrearManual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CluelessEntero extends AppCompatActivity {
    String username,enteroTheme;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    int faltanA,faltanB;
    int posA, posB;
    Button back,home,calendar,crear;
    ProgressBar progressBarEntero,progressBarCalzado;
    ImageView imageViewEntero, imageViewCalzado;
    FloatingActionButton izqEntero, derEntero, izqCalzado, derCalzado;
    ArrayList<Bitmap> entero=new ArrayList<>();
    ArrayList<Bitmap> calzado=new ArrayList<>();
    ArrayList<Integer> refIdsA=new ArrayList<>();
    ArrayList<Integer> refIdsB=new ArrayList<>();
    BaseDatosPrendas BD;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clueless_entero);
        BD=new BaseDatosPrendas(this);

        progressBarEntero=findViewById(R.id.progressBar7);
        progressBarCalzado=findViewById(R.id.progressBar8);
        imageViewEntero=findViewById(R.id.fotoArriba3);
        imageViewCalzado=findViewById(R.id.fotoZapatos2);
        izqEntero=findViewById(R.id.izqUp);
        derEntero=findViewById(R.id.derUp2);
        izqCalzado=findViewById(R.id.izqCalzado2);
        derCalzado=findViewById(R.id.derCalzado2);
        crear=findViewById(R.id.bttnCrear3);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            enteroTheme=i.getStringExtra("Entero");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        izqEntero.setEnabled(false);
        derEntero.setEnabled(false);
        izqCalzado.setEnabled(false);
        derCalzado.setEnabled(false);

        getPrendas(BD.pedirTodasFotosTipo(enteroTheme),0);
        getPrendas(BD.pedirTodasFotosTipo("Calzados"),1);

        //movimiento en entero
        derEntero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posA++;
                if(posA==entero.size()) posA=0;
                imageViewEntero.setImageBitmap(entero.get(posA));
            }
        });
        izqEntero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posA--;
                if(posA==-1) posA= entero.size()-1;
                imageViewEntero.setImageBitmap(entero.get(posA));

            }
        });

        //movimiento en parte Calzado
        derCalzado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posB++;
                if(posB==calzado.size()) posB=0;
                imageViewCalzado.setImageBitmap(calzado.get(posB));
            }
        });
        izqCalzado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posB--;
                if(posB==-1) posB= calzado.size()-1;
                imageViewCalzado.setImageBitmap(calzado.get(posB));

            }
        });

        //Crear
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faltanA == 0 && faltanB == 0) {
                    Intent intent = new Intent(CluelessEntero.this, AniadirComplementos.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Tipo", "Entero");

                    //Como es muy grande la imagen, voy a pasar su localizacion en BBDD externa
                    intent.putExtra("Entero", refIdsA.get(posA));
                    intent.putExtra("Calzado", refIdsB.get(posB));
                    intent.putExtra("PExtra", new ArrayList<Integer>());
                    intent.putExtra("id_viaje",id_viaje);
                    if(posLista!=-1){
                        intent.putExtra("veces",veces_todos);
                        intent.putExtra("vez_estancados",posLista);
                        intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
                    }
                    startActivity(intent);
                }
            }
        });

        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PantallaInicio.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PantallaCalendario.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }
    private void getPrendas(List<Integer> ids, int pos){
        //ArrayList<Bitmap> res=new ArrayList<>();
        if(pos==0)
            faltanA=ids.size();
        else
            faltanB=ids.size();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        for(int id:ids) {
            StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if(pos==0){
                        faltanA--;
                        entero.add(bm);
                        refIdsA.add(id);
                        if(faltanA==0) {
                            progressBarEntero.setVisibility(View.GONE);
                            imageViewEntero.setImageBitmap(entero.get(0));
                            izqEntero.setEnabled(true);
                            derEntero.setEnabled(true);
                            posA=0;
                        }
                    }
                    else{
                        faltanB--;
                        calzado.add(bm);
                        refIdsB.add(id);
                        if(faltanB==0) {
                            progressBarCalzado.setVisibility(View.GONE);
                            imageViewCalzado.setImageBitmap(calzado.get(0));
                            izqCalzado.setEnabled(true);
                            derCalzado.setEnabled(true);
                            posB=0;
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //SERA VERDAD
                    Toast.makeText(CluelessEntero.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
