package com.example.tfg_1.Viajes;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viaje;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MisViajes extends AppCompatActivity {
    String username;
    LinearLayout container;
    View view;
    BaseDatosOutfits BD;
    String fechaHoy;
    Button back, home, calendar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_viajes);

        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
        }
        BD=new BaseDatosOutfits(this);
        container=findViewById(R.id.linearContainer);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        Calendar cal = Calendar.getInstance();
        Date fechaActual = cal.getTime();

        // Formatear la fecha en el formato deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fechaHoy = dateFormat.format(fechaActual);

        actualizarLista();

        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PantallaInicio.class);
                intent.putExtra("username",username);
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
    private void borrarViaje(int id){
        AlertDialog.Builder elegir= new AlertDialog.Builder(MisViajes.this);
        elegir.setCancelable(false).setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("BORRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MisViajes.this, "BORRADO",Toast.LENGTH_LONG).show();
                BD.eliminarViaje(id);
                actualizarLista();
            }
        });
        AlertDialog titulo= elegir.create();
        titulo.setTitle("¿Seguro que quieres borrar tu viaje '"+BD.getViaje(id).getNombre_viaje()+"'?:");
        titulo.show();
    }
    private void actualizarLista(){
        container.removeAllViews();
        view = LayoutInflater.from(this).inflate(R.layout.listado_viajes, container, false);
        view.setBackgroundColor(ContextCompat.getColor(this,R.color.verde_nuevo));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nuevo viaje
                Intent intent=new Intent(MisViajes.this,NuevoViaje.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        container.addView(view);
        ArrayList<Viaje> misViajes=BD.todosViajes();
        //vamos a ordenarlos por ya terminados o sin terminar

        if(misViajes!=null) {
            ArrayList<Integer> finalizados=BD.viajesFinalizados(fechaHoy);
            ArrayList<Viaje> aux=new ArrayList<>();
            for (Viaje viaje : misViajes) {
                if(finalizados.contains(viaje.getId_viaje()))
                    aux.add(viaje);
                else {
                    //lees de la bd viajes guardados
                    view = LayoutInflater.from(this).inflate(R.layout.listado_viajes, container, false);
                    TextView textView = (TextView) view;
                    textView.setText(viaje.getNombre_viaje());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //viaje ya guardado
                            //Intent con Viaje viaje
                            Intent intent = new Intent(MisViajes.this, ViajeGuardado.class);
                            intent.putExtra("username", username);
                            intent.putExtra("id_viaje", viaje.getId_viaje());
                            startActivity(intent);
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            borrarViaje(viaje.getId_viaje());
                            return true;
                        }
                    });
                    container.addView(view);
                }
            }
            //los ya terminados
            for(int i=aux.size()-1;i>=0;i--){
                Viaje viaje=aux.get(i);
                //lees de la bd viajes guardados
                view = LayoutInflater.from(this).inflate(R.layout.listado_viajes, container, false);
                view.setBackgroundColor(R.drawable.para_lista_viajes_terminados);
                TextView textView = (TextView) view;
                textView.setText(viaje.getNombre_viaje());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //viaje ya guardado
                        //Intent con Viaje viaje
                        Intent intent = new Intent(MisViajes.this, ViajeGuardado.class);
                        intent.putExtra("username", username);
                        intent.putExtra("id_viaje", viaje.getId_viaje());
                        startActivity(intent);
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        borrarViaje(viaje.getId_viaje());
                        return true;
                    }
                });
                container.addView(view);
            }
        }
    }
}
