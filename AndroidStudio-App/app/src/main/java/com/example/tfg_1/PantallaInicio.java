package com.example.tfg_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tfg_1.Armario.RopaOOutfits;
import com.example.tfg_1.Viajes.MisViajes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class PantallaInicio  extends AppCompatActivity {
    String username;
    FloatingActionButton usuario, sincronizacion;
    Button armario,crear,viajes;
    Button back,home,calendar;
    ProgressBar progressBar;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    int desdeInicio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);

        usuario=findViewById(R.id.bttnuser);
        sincronizacion=findViewById(R.id.btnnActualizarBBDD);
        armario=findViewById(R.id.bttnArmario);
        crear=findViewById(R.id.bttnCrear);
        viajes=findViewById(R.id.bttnViaje);
        back=findViewById(R.id.BACK);
        calendar=findViewById(R.id.Calendar);
        progressBar=findViewById(R.id.progressBar3);

        Intent intent = getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            desdeInicio=intent.getIntExtra("i",0);
        }
        progressBar.setVisibility(View.INVISIBLE);

        //Actualizar bbdd
        //si la bbdd este vacia o tenga datos de otro usuario,
        // se instaura en la bbdd el contenido de la nube
        String persona=BD.existeOtroUsuario(username);
        int numPrendas=BD.countItems("tipoprenda");
        if(persona!=null){
            progressBar.setVisibility(View.VISIBLE);
            usuario.setEnabled(false);
            armario.setEnabled(false);
            crear.setEnabled(false);
            viajes.setEnabled(false);
            back.setEnabled(false);
            calendar.setEnabled(false);

            guardarEnFireBase(persona);
        }
        else if(numPrendas==0 && desdeInicio==1)
        {
            progressBar.setVisibility(View.VISIBLE);
            usuario.setEnabled(false);
            armario.setEnabled(false);
            crear.setEnabled(false);
            viajes.setEnabled(false);
            back.setEnabled(false);
            calendar.setEnabled(false);
            BD.vaciar();
            BDO.vaciar();
            rellenarBBDD();
            rellenarBBDDOutfit();
        }
        //Presiona boton de sincronizacion manual
        sincronizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarEnFireBase(username);
            }
        });


        //Presiona vista de perfil
        usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("informacion_usuarios/"+username);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                            Toast.makeText(PantallaInicio.this,"Error en usuario: No existe",Toast.LENGTH_SHORT).show();
                        else{

                            Intent intent = new Intent(getApplicationContext(),PerfilUsuario.class);
                            intent.putExtra("username",username);
                            intent.putExtra("password",snapshot.child("password").getValue(String.class));
                            intent.putExtra("nombreReal",snapshot.child("nombreReal").getValue(String.class));
                            intent.putExtra("email",snapshot.child("correo").getValue(String.class));
                            intent.putExtra("edad",snapshot.child("anio_nacimiento").getValue(Integer.class));
                            intent.putExtra("ciudad",snapshot.child("nombreCiudad").getValue(String.class));
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //Presiona Armario
        armario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RopaOOutfits.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        //Presiona Crear
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BD.pedirTodasFotosTipo("Calzados").isEmpty())
                    Toast.makeText(PantallaInicio.this,"Debes de tener algun tipo de Calzado guardado", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), ManualOAutomatic.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
        viajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MisViajes.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //Tabla de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaCalendario.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }
    private void guardarEnFireBase(String persona){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference();
        myRef.child("prendas/"+persona).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //ya hay informacion de ese usuario
                    //eliminar lo que tiene
                    myRef.child("prendas/"+persona).removeValue();

                }
                //añadimos la tabla de usosExtra
                myRef.child("prendas/" + persona + "/USOEXTRA").setValue(BD.getUsoExtra());

                //no hay nada de info anterior
                for(Prenda p: BD.todasPrendas()) {
                    myRef.child("prendas/" + persona + "/Prenda" + p.getId()).setValue(p);
                }
                if(!persona.equals(username)){
                    BD.vaciar();
                    rellenarBBDD();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.child("outfits/"+persona).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //ya hay informacion de ese usuario
                    //eliminar lo que tiene
                    myRef.child("outfits/"+persona).removeValue();

                }
                //no hay nada de info anterior
                for(Outfit p: BDO.todasOutfits()) {
                    myRef.child("outfits/" + persona + "/Outfit" + p.getId()).setValue(p);
                }
                myRef.child("outfits/" + persona + "/Viajes").setValue(BDO.todosViajes());
                myRef.child("outfits/" + persona + "/Dias").setValue(BDO.todosDias());

                if(persona.equals(username))
                    Toast.makeText(PantallaInicio.this,"Datos de "+username+" sincronizados.",Toast.LENGTH_LONG).show();
                else{
                    BDO.vaciar();
                    rellenarBBDDOutfit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void rellenarBBDD() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference listRef = database.getReference();
        //CAMBIAR
        listRef.child("prendas/"+username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ArrayList<Prenda> listaPrendas = new ArrayList<>();
                    ArrayList<String> extrauso = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("USOEXTRA")) {
                            extrauso = (ArrayList<String>) postSnapshot.getValue();
                        } else {
                            //prendas
                            int id=postSnapshot.child("id").getValue(Integer.class);
                            String tipo=postSnapshot.child("tipo").getValue(String.class);
                            String nombre_prenda=postSnapshot.child("nombre_prenda").getValue(String.class);
                            int lavando=postSnapshot.child("lavando").getValue(Integer.class);
                            ArrayList<String> colores= (ArrayList<String>) postSnapshot.child("colores").getValue();
                            ArrayList<String> usos= (ArrayList<String>) postSnapshot.child("usos").getValue();
                            Prenda prenda = new Prenda(id,tipo,nombre_prenda,lavando,colores,usos);
                            listaPrendas.add(prenda);
                        }

                    }

                    BD.setPrendas(listaPrendas,extrauso,username);

                }
                /*
                progressBar.setVisibility(View.GONE);
                usuario.setEnabled(true);
                armario.setEnabled(true);
                crear.setEnabled(true);
                viajes.setEnabled(true);
                back.setEnabled(true);
                calendar.setEnabled(true);

                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void rellenarBBDDOutfit() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference listRef = database.getReference();
        //CAMBIAR
        listRef.child("outfits/"+username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ArrayList<Outfit> lista = new ArrayList<>();
                    ArrayList<HashMap> listaViajes=new ArrayList<>();
                    ArrayList<HashMap> listaDias=new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("Viajes")) {
                            listaViajes = (ArrayList<HashMap>) postSnapshot.getValue();
                        }
                        else if(postSnapshot.getKey().equals("Dias")){
                            listaDias = (ArrayList<HashMap>) postSnapshot.getValue();
                        }
                        else {
                            ArrayList<Integer> idsExtra = new ArrayList<>();
                            int id = postSnapshot.child("id").getValue(Integer.class);
                            String name = postSnapshot.child("name").getValue(String.class);
                            int idUp = postSnapshot.child("idUp").getValue(Integer.class);
                            int idDown = postSnapshot.child("idDown").getValue(Integer.class);
                            int idEntero = postSnapshot.child("idEntero").getValue(Integer.class);
                            int idCalzado = postSnapshot.child("idCalzado").getValue(Integer.class);
                            //longs?
                            for (Long ids : (ArrayList<Long>) postSnapshot.child("idsExtra").getValue())
                                idsExtra.add(ids.intValue());
                            ArrayList<String> colores = (ArrayList<String>) postSnapshot.child("colores").getValue();
                            ArrayList<String> usos = (ArrayList<String>) postSnapshot.child("usos").getValue();
                            Outfit outfit = new Outfit(id, name, idUp, idDown, idEntero, idCalzado, idsExtra, colores, usos);
                            lista.add(outfit);
                        }
                    }
                    BDO.setOutfits(lista);
                    if(listaViajes.size()!=0)
                        BDO.setViajes(listaViajes);
                    if(listaDias.size()!=0)
                        BDO.setDias(listaDias);
                }
                    progressBar.setVisibility(View.GONE);
                    usuario.setEnabled(true);
                    armario.setEnabled(true);
                    crear.setEnabled(true);
                    viajes.setEnabled(true);
                    back.setEnabled(true);
                    calendar.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
