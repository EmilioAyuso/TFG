package com.example.tfg_1.CrearManual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;

import java.util.ArrayList;
import java.util.List;

public class AniadirC2 extends AppCompatActivity {
    String username,theme;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    boolean porPartes;
    int arriba,abajo,entero,calzado,pos, id_editar, categoria;
    Button back,home,calendar;
    ArrayList<Integer> PrendasExtra;
    BaseDatosPrendas BD;
    ListView listView;
    private List<String> elementos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aniadir_c2);
        BD=new BaseDatosPrendas(this);

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
            id_editar=i.getIntExtra("id",0);
            if(id_editar!=0){
                theme=i.getStringExtra("Theme");
                categoria=i.getIntExtra("Categoria",0);
            }
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }

        listView=findViewById(R.id.listaTipo);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);


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
        elementos.add("Calzado");
        elementos.add("Trajes");
        elementos.add("Abrigos");
        elementos.add("Accesorios");
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,elementos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String) adapterView.getItemAtPosition(i);
                if (BD.pedirTodasFotosTipo(item).isEmpty()) {
                    Toast.makeText(AniadirC2.this,"No tienes prendas asociadas con " + item,Toast.LENGTH_SHORT).show();;
                }
                else {
                    Intent intent = new Intent(AniadirC2.this, AniadirC3.class);
                    intent.putExtra("username", username);
                    if (porPartes) {
                        intent.putExtra("Tipo", "PorPartes");
                        intent.putExtra("PArriba", arriba);
                        intent.putExtra("PAbajo", abajo);
                    } else {
                        intent.putExtra("Tipo", "Entero");
                        intent.putExtra("Entero", entero);
                    }
                    intent.putExtra("Calzado", calzado);
                    intent.putExtra("PExtra", PrendasExtra);
                    intent.putExtra("pos", pos);
                    intent.putExtra("tipoPrenda", item);
                    intent.putExtra("id",id_editar);
                    intent.putExtra("Theme",theme);
                    intent.putExtra("Categoria",categoria);
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
}
