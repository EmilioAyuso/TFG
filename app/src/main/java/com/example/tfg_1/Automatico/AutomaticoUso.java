package com.example.tfg_1.Automatico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Armario.ListaAdapter;
import com.example.tfg_1.Armario.ListaOpciones;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.CrearManual.Manual;
import com.example.tfg_1.MainActivity;
import com.example.tfg_1.ManualOAutomatic;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viaje;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutomaticoUso extends AppCompatActivity {
    String username;
    int id_viaje,posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    TextView titulo;
    ListView lista;
    LocalDate localDate;
    String clima;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    Button back,home,calendar;
    private List<String> elementos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_opciones);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            clima=i.getStringExtra("clima");
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        titulo=findViewById(R.id.textViewTitulo);
        lista=findViewById(R.id.listaArriba);
        findViewById(R.id.bttnAceptar).setVisibility(View.GONE);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        titulo.setText("Elige un Uso:");
        titulo.setTextSize(18);


        BaseDatosPrendas BD=new BaseDatosPrendas(this);
        for(String u:BD.getUsoExtra()){
            elementos.add(u);
        }
        ListaAdapter adapter= new ListaAdapter(AutomaticoUso.this,elementos,null,false);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item= (String) adapterView.getItemAtPosition(i);
                identificarProblemas(item);
            }
        });

        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ManualOAutomatic.class);
                intent.putExtra("username",username);
                intent.putExtra("id_viaje",id_viaje);
                intent.putExtra("vez_estancados",posLista);
                intent.putExtra("veces",veces_todos);
                intent.putExtra("outfits_anteriores",idsSelecionados);
                startActivity(intent);
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
    
    private void identificarProblemas(String item){

        boolean partesArribaCortas=(!BD.pedirTodasFotosTipoUso("Camisetas",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Camisas/Blusas",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Polos",item).isEmpty());
        boolean partesAbajoCortas=(!BD.pedirTodasFotosTipoUso("Pantalones Cortos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Bañadores",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Faldas",item).isEmpty());
        boolean partesAbajoLargas=(!BD.pedirTodasFotosTipoUso("Pantalones Largos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Faldas",item).isEmpty());
        boolean partesCompletas=(!BD.pedirTodasFotosTipoUso("Vestidos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Monos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Trajes",item).isEmpty());
        boolean partesAbrigoMedio=(!BD.pedirTodasFotosTipoUso("Jerseis",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Sudaderas",item).isEmpty());
        boolean abrigo=(!BD.pedirTodasFotosTipoUso("Abrigos",item).isEmpty());

        if(BD.pedirTodasFotosTipoUso("Calzados",item).isEmpty())
            Toast.makeText(this, "Necesitas calzado disponible del tipo "+item, Toast.LENGTH_SHORT).show();
        else if(clima.startsWith("Verano")){
            if(clima.equals("Verano con") && !abrigo)//si no tenemos abrigo y llueve mucho
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(clima.equals("Verano floja") && !partesAbrigoMedio)//si no tenemos jersey/sudadera y llueve flojo
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+ item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoCortas) )//sino podemos hacer un outfit completo
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo corta, de tipo "+item, Toast.LENGTH_LONG).show();
            else
                hacerIntent(item);
        }
        else if(clima.startsWith("Primavera")){
            if(clima.equals("Primavera con") && !abrigo)//si no tenemos abrigo y llueve
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesAbrigoMedio)
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoLargas))
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo larga de tipo "+item, Toast.LENGTH_LONG).show();
            else
                hacerIntent(item);
        }
        else{
            //otoño-invierno
            if(!abrigo)//si no tenemos abrigo
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesAbrigoMedio)
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoLargas))
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo larga de tipo "+item, Toast.LENGTH_LONG).show();
            else
                hacerIntent(item);
        }
        
    }
    private void hacerIntent(String item){
        Intent intent = new Intent(AutomaticoUso.this, GeneradorOutfit.class);
        intent.putExtra("username", username);
        intent.putExtra("uso", item);
        intent.putExtra("clima", clima);
        intent.putExtra("id_viaje", id_viaje);
        if (posLista != -1) {
            intent.putExtra("veces", veces_todos);
            intent.putExtra("vez_estancados", posLista);
            intent.putIntegerArrayListExtra("outfits_anteriores", idsSelecionados);
        }
        startActivity(intent);
    }


}
