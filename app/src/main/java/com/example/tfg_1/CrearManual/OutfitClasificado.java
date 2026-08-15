package com.example.tfg_1.CrearManual;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Armario.GaleryActivity;
import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.Outfits;
import com.example.tfg_1.Armario.PrendaAdapter;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.Armario.Prendas;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.ManualOAutomatic;
import com.example.tfg_1.Outfit;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OutfitClasificado extends AppCompatActivity {
    String username, theme;
    TextView name;
    ProgressBar progressBar;
    GridView fotos;
    List<Integer> idFotos, listaOrdenid;
    String diaSeleccionado;
    Button back,home,calendar,aniadir;
    int categoria, faltan, id_viaje;
    BaseDatosOutfits BD;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prenda_clasificada);
        BD=new BaseDatosOutfits(this);

        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            id_viaje=intent.getIntExtra("id_viaje",0);
            diaSeleccionado=intent.getStringExtra("dia");
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                diaSeleccionado=intent.getParcelableExtra("dia", LocalDate.class);
            }
             */
        }
        name=findViewById(R.id.txttheme);
        name.setText(theme);

        //aniadir
        aniadir=findViewById(R.id.buttonAñadir);
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diaSeleccionado==null) {
                    Intent intent = new Intent(OutfitClasificado.this, ManualOAutomatic.class);
                    intent.putExtra("username", username);
                    if (id_viaje != 0)
                        intent.putExtra("id_viaje", id_viaje);
                    startActivity(intent);
                }
            }
        });
        //actualizar fotos
        fotos=findViewById(R.id.listaRopa);
        List<byte[]> listafotos=new ArrayList<>();
        idFotos=null;
        listaOrdenid=new ArrayList<>();
        if(theme.equals("TODOS")){
            idFotos=BD.pedirTodosOutfits();
        } else if(categoria==1){
            idFotos = BD.pedirTodosOutfitsUso(theme);
        } else if (categoria==2) {
            idFotos=BD.pedirTodosOutfitsColor(theme);
        }
        progressBar=findViewById(R.id.progressBar4);
        if(idFotos==null)
            progressBar.setVisibility(View.INVISIBLE);
        else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            faltan=idFotos.size();
            progressBar.setVisibility(View.VISIBLE);
            for (int id : idFotos) {
                StorageReference storageRef = storage.getReference();
                StorageReference islandRef=storageRef.child("outfits/"+username+"/"+id);
                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        listafotos.add(bytes);
                        listaOrdenid.add(id);
                        PrendaAdapter adapter= new PrendaAdapter(OutfitClasificado.this,listafotos);
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
                if(diaSeleccionado!=null){
                    int id = listaOrdenid.get(i);
                    //desde calendario
                    //aniadimos foto a fecha, y volvemos a calendario
                    BD.addDayOutfit(diaSeleccionado,id);
                    Intent intent = new Intent(getApplicationContext(), PantallaCalendario.class);
                    intent.putExtra("username", username);
                    intent.putExtra("dia", diaSeleccionado);
                    startActivity(intent);
                }
                else if(id_viaje==0) {
                    int id = listaOrdenid.get(i);
                    Intent intent = new Intent(getApplicationContext(), InfoOutfit.class);
                    intent.putExtra("id", id);
                    intent.putExtra("username", username);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
                    startActivity(intent);
                }
                else{
                    //añadimos esa foto al viaje
                    int id_outfit = listaOrdenid.get(i);
                    ArrayList<Integer> ids_outfit=new ArrayList<>();
                    ids_outfit.add(id_outfit);
                    BD.editarOutfitsViajes(id_viaje, ids_outfit);
                    //volvemos al viaje
                    Intent intent=new Intent(OutfitClasificado.this, ViajeGuardado.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id_viaje",id_viaje);
                    startActivity(intent);
                }
            }
        });

        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Outfits.class);
                intent.putExtra("username",username);
                intent.putExtra("id_viaje", id_viaje);
                if(diaSeleccionado!=null){
                    intent.putExtra("dia",diaSeleccionado);
                }
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
