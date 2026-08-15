package com.example.tfg_1.Viajes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;

import java.util.ArrayList;

public class ElegirTipos extends AppCompatActivity {
    String username, nV,local;
    int ini,fin,mal;
    ListView list;
    Button back, home,calendar, continuar;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    int[] cuantos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.elegir_tipos);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);
        Intent intent =getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            nV=intent.getStringExtra("nombre");
            local=intent.getStringExtra("localizacion");
            ini=intent.getIntExtra("inicio",0);
            fin=intent.getIntExtra("fin",0);
            mal=intent.getIntExtra("maleta",0);
        }

        list=findViewById(R.id.listView);
        continuar=findViewById(R.id.button3);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        ArrayList<String> usos=BD.getUsoExtra();
        ListaAdapterSumRest adapter=new ListaAdapterSumRest(this,usos);
        list.setAdapter(adapter);


        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuantos=adapter.getContador();
                if(hayAlguno()){
                    Intent intent= new Intent(ElegirTipos.this,Seleccion_Outfits.class);
                    int id=BDO.introducirViaje(nV,local,conversionFecha(ini),conversionFecha(fin),conversionFecha(mal),null);
                    if(id!=-1){
                        intent.putExtra("username",username);
                        intent.putExtra("id_viaje",id);
                        intent.putExtra("veces",cuantos);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(ElegirTipos.this,"Error en BBDD",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ElegirTipos.this,"Selecciona algun uso",Toast.LENGTH_SHORT).show();
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

    /**
     * @return True if se ha seleccionado algun uso de outfit
     */
    private boolean hayAlguno(){
        for (int t:cuantos)
            if(t!=0) return true;
        return false;
    }
    public static String conversionFecha(int fecha){
        int anio=fecha/10000;
        int mes=(fecha-anio*10000)/100;
        int dia=(fecha-anio*10000-mes*100);
        String str_mes=String.valueOf(mes);
        String str_dia=String.valueOf(dia);
        if(mes<10) str_mes="0"+str_mes;
        if(dia<10) str_dia="0"+str_dia;
        return (String.valueOf(anio)+"-"+str_mes+"-"+str_dia);
    }
}
