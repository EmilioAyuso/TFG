package com.example.tfg_1.Armario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;

import java.util.ArrayList;
import java.util.List;


public class ListaOpciones extends AppCompatActivity {
    String username,theme,editName;
    int categoria;
    Button back,home,calendar, aceptar;
    Uri imagenUri;
    TextView titulo;
    ListView listView;
    String tipo;
    ArrayList<String> colores=new ArrayList<>();
    ArrayList<String> uso=new ArrayList<>();
    List<Integer> destacados=new ArrayList<>();
    private List<String> elementos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_opciones);

        Intent intent=getIntent();
        if(intent!=null && intent.hasExtra("imagen")){
            username=intent.getStringExtra("username");
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            imagenUri=intent.getParcelableExtra("imagen");
            colores=intent.getStringArrayListExtra("ListaColores");
            uso=intent.getStringArrayListExtra("ListaUsos");
            editName=intent.getStringExtra("Update");
        }
        titulo=findViewById(R.id.textViewTitulo);
        listView=findViewById(R.id.listaArriba);
        aceptar=findViewById(R.id.bttnAceptar);
        aceptar.setVisibility(View.INVISIBLE);

        if(categoria!=1 || !editName.equals(""))
            primerCaso();
        else{
            tipo=theme;
            segundoCaso();
        }

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
    private void primerCaso(){
        titulo.setText("Tipo de prenda:");
        elementos.add("Camisetas");
        elementos.add("Camisas/Blusas");
        elementos.add("Polos");
        elementos.add("Pantalones Largos");
        elementos.add("Pantalones Cortos");
        elementos.add("Bañadores");
        elementos.add("Faldas");
        elementos.add("Vestidos");
        elementos.add("Monos");
        elementos.add("Jerseis");
        elementos.add("Sudaderas");
        elementos.add("Calzados");
        elementos.add("Trajes");
        elementos.add("Abrigos");
        elementos.add("Accesorios");
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,elementos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tipo=(String)adapterView.getItemAtPosition(i);
                elementos.clear();
                segundoCaso();
            }
        });

    }
    private void segundoCaso(){
        titulo.setText("Color/es:");

        elementos.add("Rojo");
        elementos.add("Naranja");
        elementos.add("Amarillo");
        elementos.add("Verde Claro");
        elementos.add("Verde Oscuro");
        elementos.add("Azul Claro");
        elementos.add("Azul");
        elementos.add("Azul Marino");
        elementos.add("Morado");
        elementos.add("Rosa");
        elementos.add("Beige");
        elementos.add("Marron");
        elementos.add("Gris");
        elementos.add("Blanco");
        elementos.add("Negro");
        aceptar.setVisibility(View.VISIBLE);

        for (String s:colores) {
            destacados.add(elementos.indexOf(s));
        }
        ListaAdapter adapter= new ListaAdapter(this,elementos,destacados,true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String)adapterView.getItemAtPosition(i);
                if(colores.contains(item)){
                    colores.remove(item);
                    destacados.remove((Integer)i);
                    ListaAdapter adapter= new ListaAdapter(ListaOpciones.this,elementos,destacados,true);
                    listView.setAdapter(adapter);
                }

                else {
                    colores.add(item);
                    destacados.add(i);
                    ListaAdapter adapter= new ListaAdapter(ListaOpciones.this,elementos,destacados,true);
                    listView.setAdapter(adapter);
                }
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colores.isEmpty()) {
                    elementos.clear();
                    destacados.clear();
                    tercerCaso();
                }
                else
                    Toast.makeText(ListaOpciones.this,"SELECCIONA ALGUN COLOR",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void tercerCaso(){

        titulo.setText("Uso de prenda:");
        BaseDatosPrendas BD=new BaseDatosPrendas(this);
        for(String u:BD.getUsoExtra()){
            elementos.add(u);
        }
        for (String s:uso) {
            destacados.add(elementos.indexOf(s));
        }
        ListaAdapter adapter= new ListaAdapter(ListaOpciones.this,elementos,destacados,false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String)adapterView.getItemAtPosition(i);
                if(uso.contains(item)){
                    uso.remove(item);
                    destacados.remove((Integer)i);
                    ListaAdapter adapter= new ListaAdapter(ListaOpciones.this,elementos,destacados,false);
                    listView.setAdapter(adapter);
                }
                else
                {
                    uso.add(item);
                    destacados.add(i);
                    ListaAdapter adapter= new ListaAdapter(ListaOpciones.this,elementos,destacados,false);
                    listView.setAdapter(adapter);
                }

            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!uso.isEmpty()) {
                    //intent con los datos anteriores +tipo+uso+colores
                    Intent intent=new Intent(getApplicationContext(),EtiquetarPrenda.class);
                    intent.putExtra("username",username);
                    intent.putExtra("imagen",imagenUri);
                    intent.putExtra("tipo",tipo);
                    intent.putStringArrayListExtra("colores",colores);
                    intent.putStringArrayListExtra("uso",uso);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
                    intent.putExtra("Name/Update",editName);
                    startActivity(intent);
                }
                else
                    Toast.makeText(ListaOpciones.this,"SELECCIONA ALGUN USO",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
