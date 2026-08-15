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

import java.util.concurrent.CountDownLatch;


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

public class CluelessPartes extends AppCompatActivity {
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    String username,pArriba,pAbajo;
    int faltanA,faltanB,faltanC;
    int posA, posB, posC;
    Button back,home,calendar, crear;
    ProgressBar progressBarUp,progressBarDown,progressBarCalzado;
    ImageView imageViewUp, imageViewDown, imageViewCalzado;
    FloatingActionButton izqUp, derUp, izqDown, derDown, izqCalzado, derCalzado;
    ArrayList<Bitmap> arriba=new ArrayList<>();
    ArrayList<Bitmap> abajo=new ArrayList<>();
    ArrayList<Bitmap> calzado=new ArrayList<>();
    ArrayList<Integer> refIdsA=new ArrayList<>();
    ArrayList<Integer> refIdsB=new ArrayList<>();
    ArrayList<Integer> refIdsC=new ArrayList<>();
    BaseDatosPrendas BD;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cluelesspartes);
        BD=new BaseDatosPrendas(this);

        progressBarUp=findViewById(R.id.progressBarUP);
        progressBarDown=findViewById(R.id.progressBarDOWN);
        progressBarCalzado=findViewById(R.id.progressBarCalzado);
        imageViewUp=findViewById(R.id.fotoArriba);
        imageViewDown=findViewById(R.id.fotoAbajo);
        imageViewCalzado=findViewById(R.id.fotoZapatos);
        izqUp=findViewById(R.id.IzqUp);
        derUp=findViewById(R.id.derUp);
        izqDown=findViewById(R.id.izqDown);
        derDown=findViewById(R.id.derDown);
        izqCalzado=findViewById(R.id.izqCalzado);
        derCalzado=findViewById(R.id.derCalzado);
        crear=findViewById(R.id.bttnCrear2);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            pArriba=i.getStringExtra("Arriba");
            pAbajo=i.getStringExtra("Abajo");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        izqUp.setEnabled(false);
        derUp.setEnabled(false);
        izqDown.setEnabled(false);
        derDown.setEnabled(false);
        izqCalzado.setEnabled(false);
        derCalzado.setEnabled(false);

        getPrendas(BD.pedirTodasFotosTipo(pArriba),0);
        getPrendas(BD.pedirTodasFotosTipo(pAbajo),1);
        getPrendas(BD.pedirTodasFotosTipo("Calzados"),2);

        //movimiento en parte Arriba
        derUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posA++;
                if(posA==arriba.size()) posA=0;
                imageViewUp.setImageBitmap(arriba.get(posA));
            }
        });
        izqUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posA--;
                if(posA==-1) posA= arriba.size()-1;
                imageViewUp.setImageBitmap(arriba.get(posA));

            }
        });

        //movimiento en parte Abajo
        derDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posB++;
                if(posB==abajo.size()) posB=0;
                imageViewDown.setImageBitmap(abajo.get(posB));
            }
        });
        izqDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posB--;
                if(posB==-1) posB= abajo.size()-1;
                imageViewDown.setImageBitmap(abajo.get(posB));

            }
        });
        //movimiento en parte Calzado
        derCalzado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posC++;
                if(posC==calzado.size()) posC=0;
                imageViewCalzado.setImageBitmap(calzado.get(posC));
            }
        });
        izqCalzado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posC--;
                if(posC==-1) posC= calzado.size()-1;
                imageViewCalzado.setImageBitmap(calzado.get(posC));

            }
        });

        //Crear
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faltanA == 0 && faltanB == 0 && faltanC == 0) {
                    Intent intent = new Intent(CluelessPartes.this, AniadirComplementos.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Tipo", "PorPartes");
                    intent.putExtra("PArriba", refIdsA.get(posA));
                    intent.putExtra("PAbajo", refIdsB.get(posB));
                    intent.putExtra("Calzado", refIdsC.get(posC));
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
        switch(pos) {
            case 0: faltanA=ids.size();break;
            case 1: faltanB=ids.size();break;
            default: faltanC=ids.size();
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        for(int id:ids) {
            StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    switch(pos) {
                        case 0: faltanA--;
                            arriba.add(bm);
                            refIdsA.add(id);
                            if(faltanA==0) {
                                progressBarUp.setVisibility(View.GONE);
                                imageViewUp.setImageBitmap(arriba.get(0));
                                izqUp.setEnabled(true);
                                derUp.setEnabled(true);
                                posA=0;
                            }
                            break;
                        case 1: faltanB--;
                            abajo.add(bm);
                            refIdsB.add(id);
                            if(faltanB==0) {
                                progressBarDown.setVisibility(View.GONE);
                                imageViewDown.setImageBitmap(abajo.get(0));
                                izqDown.setEnabled(true);
                                derDown.setEnabled(true);
                                posB=0;
                            }
                            break;
                        default: faltanC--;
                            calzado.add(bm);
                            refIdsC.add(id);
                            if(faltanC==0) {
                                progressBarCalzado.setVisibility(View.GONE);
                                imageViewCalzado.setImageBitmap(calzado.get(0));
                                izqCalzado.setEnabled(true);
                                derCalzado.setEnabled(true);
                                posC=0;
                            }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //SERA VERDAD
                    Toast.makeText(CluelessPartes.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
