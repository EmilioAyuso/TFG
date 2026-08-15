package com.example.tfg_1.Viajes;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.PrendaAdapter;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.ManualOAutomatic;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Seleccion_Outfits extends AppCompatActivity {
    String username;
    int id_viaje, vez, posLista, left;
    int[] veces_todos;
    TextView titulo,faltan;
    ProgressBar progressBar;
    GridView fotos;
    Button back, home,calendar, crear;
    ArrayList<String> usos;
    List<Integer> idFotos, listaOrdenid;
    ArrayList<Integer>idsSelecionados, idsPorAhora;
    List<Boolean> seleccionados;
    BaseDatosPrendas BDP;
    BaseDatosOutfits BDO;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccion_outfits);
        BDP=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);
        Intent intent =getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            id_viaje=intent.getIntExtra("id_viaje",0);
            veces_todos= intent.getIntArrayExtra("veces");
            posLista=intent.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                idsSelecionados=intent.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        progressBar=findViewById(R.id.progressBar9);
        titulo=findViewById(R.id.txttheme4);
        faltan=findViewById(R.id.textView15);
        fotos=findViewById(R.id.listaRopa);
        crear=findViewById(R.id.buttonCrear);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        usos=BDP.getUsoExtra();
        createNotificationChannel();

        //desde crear
        if(posLista!=-1){
            vez=veces_todos[posLista];
        }
        //desde el principio
        else{
            posLista=0;
            idsSelecionados=new ArrayList<>();
            vez=buscarSiguiente();
        }
        if(vez==0){
            //terminamos
        }
        nuevoUso();

        //tocar una foto
        fotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id= listaOrdenid.get(i);
                int posi=listaOrdenid.indexOf(id);
                if(seleccionados.get(posi)){
                    if(vez==0) {
                        faltan.setBackgroundColor(ContextCompat.getColor(Seleccion_Outfits.this, R.color.white));
                        faltan.setTextColor(ContextCompat.getColor(Seleccion_Outfits.this,R.color.black));
                    }
                    view.findViewById(R.id.tick).setVisibility(View.INVISIBLE);
                    seleccionados.remove(posi);
                    seleccionados.add(posi,false);
                    faltan.setText("Selecciona "+ ++vez);
                    idsPorAhora.remove((Integer)id);

                }
                else if (vez!=0){
                    view.findViewById(R.id.tick).setVisibility(View.VISIBLE);
                    seleccionados.remove(posi);
                    seleccionados.add(posi,true);
                    faltan.setText("Selecciona "+ --vez);
                    idsPorAhora.add(id);
                    if(vez==0){
                        faltan.setText("CONTINUAR");
                        faltan.setTextColor(ContextCompat.getColor(Seleccion_Outfits.this,R.color.white));
                        faltan.setBackgroundColor(ContextCompat.getColor(Seleccion_Outfits.this,R.color.verde_nuevo));
                    }
                }
            }
        });
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Guardamos todos los datos necesarios en ele intent para no perder el progreso
                Intent intent=new Intent(Seleccion_Outfits.this, ManualOAutomatic.class);
                intent.putExtra("username",username);
                intent.putExtra("id_viaje",id_viaje);
                intent.putExtra("veces",veces_todos);
                intent.putExtra("vez_estancados",posLista);
                intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
                startActivity(intent);
            }
        });
        faltan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vez==0){
                    for(int id:idsPorAhora)
                        if(!idsSelecionados.contains(id))
                            idsSelecionados.add(id);
                    posLista++;
                    vez=buscarSiguiente();
                    if(vez!=0) {
                        faltan.setBackgroundColor(ContextCompat.getColor(Seleccion_Outfits.this, R.color.white));
                        faltan.setTextColor(ContextCompat.getColor(Seleccion_Outfits.this,R.color.black));
                        nuevoUso();
                    }
                    else
                    {
                        //terminamos de crear viaje
                        BDO.editarOutfitsViajes(id_viaje,idsSelecionados);
                        //ponemos la alarma
                        ponerAlarma();

                        //volvemos a menu
                        Intent intent=new Intent(Seleccion_Outfits.this,MisViajes.class);
                        intent.putExtra("username",username);
                        startActivity(intent);
                        //salimos
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
    private int buscarSiguiente(){
        for(;posLista<veces_todos.length;posLista++){
            if(veces_todos[posLista]!=0) return veces_todos[posLista];
        }
        return 0;
    }
    private void nuevoUso(){
        //ponemos el titulo y el num outfits restantes
        titulo.setText(usos.get(posLista));
        faltan.setText("Selecciona "+vez);

        //pedimos los outfits con esa etiqueta
        List<byte[]> listafotos=new ArrayList<>();
        listaOrdenid=new ArrayList<>();
        idFotos=BDO.pedirTodosOutfitsUso(usos.get(posLista));
        if(idFotos==null) {
            progressBar.setVisibility(View.INVISIBLE);
            PrendaAdapter adapter= new PrendaAdapter(Seleccion_Outfits.this,listafotos);
            fotos.setAdapter(adapter);
        }
        else{
            //los ponemos en fotos desde la bbdd externa
            FirebaseStorage storage = FirebaseStorage.getInstance();
            left=idFotos.size();
            seleccionados=new ArrayList<>();
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
                        seleccionados.add(false);
                        PrendaAdapter adapter= new PrendaAdapter(Seleccion_Outfits.this,listafotos);
                        fotos.setAdapter(adapter);
                        left--;
                        if(left==0)
                            progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            PrendaAdapter adapter= new PrendaAdapter(this,listafotos);
            fotos.setAdapter(adapter);
            idsPorAhora=new ArrayList<>();}
    }

    /**
     * Pone la alarma para que el dia de la elaboracion de maleta te llegue la notificacion
     */
    private void ponerAlarma(){

        Intent intent = new Intent(this, AlarmaMaleta.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String fecha_maleta=BDO.getViaje(id_viaje).getFecha_maleta();
        Calendar cld = Calendar.getInstance();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;

        try {
            date = inputFormat.parse(fecha_maleta);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            cld.setTime(date);
            if(LocalDate.now().isEqual(LocalDate.parse(fecha_maleta))){

                cld.set(Calendar.HOUR_OF_DAY,0);
                cld.set(Calendar.MINUTE, 0);
                cld.set(Calendar.SECOND, 0);

            }
            else {
                cld.set(Calendar.HOUR_OF_DAY, 9);
                cld.set(Calendar.MINUTE, 0);
                cld.set(Calendar.SECOND, 0);
            }

            // Programar la alarma
            alarmManager.set(AlarmManager.RTC_WAKEUP, cld.getTimeInMillis(), pendingIntent);
        }
    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "canal Alarma";
            String description = "canal para la alarma";
            NotificationChannel channel = new NotificationChannel("foxandroid",name,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }
}
