package com.example.tfg_1.Automatico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Armario.PrendaAdapter;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.CrearManual.AniadirComplementos;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AniadirC3 extends AppCompatActivity {
    String username,estiloActual,tipo,clima,uso;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    boolean porPartes;
    int arriba,abajo,entero,calzado,pos,faltan, id_editar, categoria;
    Button back,home,calendar;
    ArrayList<Integer> PrendasExtra, listaOrdenid;
    BaseDatosPrendas BD;
    TextView name;
    GridView fotos;
    List<Integer> idFotos;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aniadir_c3);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            porPartes=false;
            if (i.getStringExtra("Tipo").equals("PorPartes")) {
                porPartes=true;
                arriba = i.getIntExtra("PArriba",0);
                abajo = i.getIntExtra("PAbajo",0);
            }
            else
                entero = i.getIntExtra("Entero",0);
            calzado = i.getIntExtra("Calzado",0);
            PrendasExtra= i.getIntegerArrayListExtra("PExtra");
            pos=i.getIntExtra("pos",-1);
            tipo=i.getStringExtra("tipoPrenda");
            clima=i.getStringExtra("clima");
            uso=i.getStringExtra("uso");
            estiloActual=i.getStringExtra("style");

            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        BD=new BaseDatosPrendas(this);

        name=findViewById(R.id.txttheme2);
        name.setText(tipo);
        progressBar=findViewById(R.id.progressBar6);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        fotos=findViewById(R.id.listaRopa);
        List<byte[]> listafotos=new ArrayList<>();
        listaOrdenid=new ArrayList<>();
        idFotos=BD.pedirTodasFotosTipo(tipo);
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
                    PrendaAdapter adapter= new PrendaAdapter(AniadirC3.this,listafotos);
                    fotos.setAdapter(adapter);
                    faltan--;
                    if(faltan==0)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        PrendaAdapter adapter= new PrendaAdapter(this,listafotos);
        fotos.setAdapter(adapter);

        //tocar una foto
        fotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id= listaOrdenid.get(i);
                if(PrendasExtra.isEmpty()){
                    PrendasExtra.add(0);
                    PrendasExtra.add(0);
                    PrendasExtra.add(0);
                }
                //añadimos id a PrendasExtra
                PrendasExtra.remove(pos);
                PrendasExtra.add(pos,id);


                Intent intent=new Intent(getApplicationContext(), GeneradorOutfit.class);
                intent.putExtra("username",username);
                if(porPartes){
                    intent.putExtra("Tipo","PorPartes");
                    intent.putExtra("PArriba",arriba);
                    intent.putExtra("PAbajo",abajo);
                }
                else{
                    intent.putExtra("Tipo","Entero");
                    intent.putExtra("Entero",entero);
                }

                intent.putExtra("Calzado",calzado);
                intent.putExtra("PExtra",PrendasExtra);
                intent.putExtra("uso",uso);
                intent.putExtra("clima",clima);
                intent.putExtra("style",estiloActual);
                intent.putExtra("id_viaje",id_viaje);
                if(posLista!=-1){
                    intent.putExtra("veces",veces_todos);
                    intent.putExtra("vez_estancados",posLista);
                    intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
                }
                startActivity(intent);
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
}
