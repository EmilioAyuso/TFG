package com.example.tfg_1.CrearManual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Armario.ListaAdapter;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;

import java.util.ArrayList;
import java.util.List;

public class Manual extends AppCompatActivity {
    String username, parte_arriba, parte_abajo;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    Button back,home,calendar;
    ListView pArriba, pAbajo;
    List<Integer> destacados1=new ArrayList<>();
    List<Integer> destacados2=new ArrayList<>();
    TextView p1,p2;
    boolean existeCalzado=true, porPartes=true;
    ListaAdapter adapterUp;
    ListaAdapter adapterDown;
    private List<String> elementosUp = new ArrayList<>();
    private List<String> elementosDown = new ArrayList<>();
    RadioGroup eleccion;
    BaseDatosPrendas BD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partes);

        pArriba=findViewById(R.id.listaArriba);
        pAbajo=findViewById(R.id.listaAbajo);
        eleccion=findViewById(R.id.radiogroup);
        p1=findViewById(R.id.textView11);
        p2=findViewById(R.id.textView12);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);
        BD=new BaseDatosPrendas(this);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }

        elementosUp.add("Camisetas");
        elementosUp.add("Camisas/Blusas");
        elementosUp.add("Polos");
        elementosUp.add("Jerseis");
        elementosUp.add("Sudaderas");
        elementosUp.add("Abrigos");
        adapterUp= new ListaAdapter(Manual.this,elementosUp,destacados1,false);
        pArriba.setAdapter(adapterUp);

        elementosDown.add("Pantalones Largos");
        elementosDown.add("Pantalones Cortos");
        elementosDown.add("Bañadores");
        elementosDown.add("Faldas");

        adapterDown = new ListaAdapter(Manual.this,elementosDown,destacados2,false);
        pAbajo.setAdapter(adapterDown);

        eleccion.setOnCheckedChangeListener((radioGroup, i1) -> {
            elementosUp.clear();
            if(i1==R.id.radioPartes){
                p1.setText("Parte de Arriba");
                elementosUp.add("Camisetas");
                elementosUp.add("Camisas/Blusas");
                elementosUp.add("Polos");
                elementosUp.add("Jerseis");
                elementosUp.add("Sudaderas");
                elementosUp.add("Abrigos");
                adapterUp= new ListaAdapter(Manual.this,elementosUp,destacados1,false);
                pArriba.setAdapter(adapterUp);
                pAbajo.setVisibility(View.VISIBLE);
                pAbajo.setEnabled(true);
                p2.setVisibility(View.VISIBLE);
                porPartes=true;
                destacados1.clear();
            }
            else{
                p1.setText("Partes Enteras");
                elementosUp.add("Vestidos");
                elementosUp.add("Monos");
                elementosUp.add("Trajes");
                adapterUp= new ListaAdapter(Manual.this,elementosUp,destacados1,false);
                pArriba.setAdapter(adapterUp);
                pAbajo.setVisibility(View.INVISIBLE);
                pAbajo.setEnabled(false);
                p2.setVisibility(View.INVISIBLE);
                porPartes=false;
                destacados1.clear();
            }
        });




        pArriba.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String)adapterView.getItemAtPosition(i);
                if(destacados1.isEmpty()){
                    if(BD.pedirTodasFotosTipo(item).isEmpty())
                        Toast.makeText(Manual.this,"Debes de tener algun tipo de "+item+" guardado", Toast.LENGTH_SHORT).show();
                    else {
                        //no hay ninguno tocado
                        destacados1.add(i);
                        parte_arriba = item;
                        ListaAdapter adapterUp = new ListaAdapter(Manual.this, elementosUp, destacados1, false);
                        pArriba.setAdapter(adapterUp);
                        if (!porPartes ||!destacados2.isEmpty())
                            hacerIntent();
                    }
                }
                else if(parte_arriba.equals(item)){
                    //has tocado el destacado
                    parte_arriba=null;
                    destacados1.clear();
                    ListaAdapter adapterUp= new ListaAdapter(Manual.this,elementosUp,destacados1,false);
                    pArriba.setAdapter(adapterUp);
                }
                else
                {
                    //tocas 1 habiendo tocado ya otro
                    if(BD.pedirTodasFotosTipo(item).isEmpty())
                        Toast.makeText(Manual.this,"Debes de tener algun tipo de "+item+" guardado", Toast.LENGTH_SHORT).show();
                    else {
                        destacados1.clear();
                        destacados1.add(i);
                        parte_arriba = item;
                        ListaAdapter adapterUp = new ListaAdapter(Manual.this, elementosUp, destacados1, false);
                        pArriba.setAdapter(adapterUp);
                        if (!destacados2.isEmpty())
                            hacerIntent();
                    }
                }
            }
        });
        pAbajo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String)adapterView.getItemAtPosition(i);
                if(destacados2.isEmpty()){
                    //no hay ninguno tocado
                    if(BD.pedirTodasFotosTipo(item).isEmpty())
                        Toast.makeText(Manual.this,"Debes de tener algun tipo de "+item+" guardado", Toast.LENGTH_SHORT).show();
                    else {
                        destacados2.add(i);
                        parte_abajo = item;
                        ListaAdapter adapterUp = new ListaAdapter(Manual.this, elementosDown, destacados2, false);
                        pAbajo.setAdapter(adapterUp);
                        if (!destacados1.isEmpty())
                            hacerIntent();
                    }
                }
                else if(parte_abajo.equals(item)){
                    //has tocado el destacado
                    parte_abajo=null;
                    destacados2.clear();
                    ListaAdapter adapterUp= new ListaAdapter(Manual.this,elementosDown,destacados2,false);
                    pAbajo.setAdapter(adapterUp);
                }
                else
                {
                    //tocas 1 habiendo tocado ya otro
                    if(BD.pedirTodasFotosTipo(item).isEmpty())
                        Toast.makeText(Manual.this,"Debes de tener algun tipo de "+item+" guardado", Toast.LENGTH_SHORT).show();
                    else {
                        destacados2.clear();
                        destacados2.add(i);
                        parte_abajo = item;
                        ListaAdapter adapterUp = new ListaAdapter(Manual.this, elementosDown, destacados2, false);
                        pAbajo.setAdapter(adapterUp);
                        if (!destacados1.isEmpty())
                            hacerIntent();
                    }
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
    private void hacerIntent(){
        Intent intent;
        if(porPartes){
            intent=new Intent(getApplicationContext(),CluelessPartes.class);
            intent.putExtra("Arriba", parte_arriba);
            intent.putExtra("Abajo", parte_abajo);
        }
        else{
            intent=new Intent(getApplicationContext(),CluelessEntero.class);
            intent.putExtra("Entero", parte_arriba);
        }
        intent.putExtra("username",username);
        intent.putExtra("id_viaje",id_viaje);
        if(posLista!=-1){
            intent.putExtra("veces",veces_todos);
            intent.putExtra("vez_estancados",posLista);
            intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
        }
        startActivity(intent);
    }
}
