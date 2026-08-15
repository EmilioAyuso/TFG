package com.example.tfg_1.Armario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.CrearManual.OutfitClasificado;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Outfits extends AppCompatActivity {
    String username;
    RadioGroup radioGroup;
    ListView listView;
    Button back,home,calendar;
    int categoria, id_viaje;
    String diaSeleccionado;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    ListaAdapter adapter;
    private List<String> elementos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfits);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);

        Intent intent =getIntent();
        if(intent!=null) {
            username = intent.getStringExtra("username");
            id_viaje=intent.getIntExtra("id_viaje",0);
            diaSeleccionado=intent.getStringExtra("dia");
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                diaSeleccionado=intent.getParcelableExtra("dia",LocalDate.class);
            }*/
        }

        radioGroup=findViewById(R.id.radioGroup);
        listView=findViewById(R.id.listView);

        elementos.add("AÑADIR OTRO USO");
        elementos.add("TODOS");
        for(String u:BD.getUsoExtra())
            elementos.add(u);
        categoria=1;
        adapter= new ListaAdapter(this,elementos,null, false);
        listView.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            elementos.clear();
            if(i==R.id.radioUso){
                elementos.add("AÑADIR OTRO USO");
                elementos.add("TODOS");
                for(String u:BD.getUsoExtra())
                    elementos.add(u);
                categoria=1;
                adapter= new ListaAdapter(this,elementos,null, false);
                listView.setAdapter(adapter);
            }
            else if (i == R.id.radioColor) {

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
                categoria=2;
                ListaAdapter adapter= new ListaAdapter(this,elementos,null,true);
                listView.setAdapter(adapter);
            }
        });
        //Accionas a un elemento de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String) adapterView.getItemAtPosition(i);
                if(item.equals("AÑADIR OTRO USO")){
                    //añadir mas usos
                    ventanaEmergenteAniadir(Outfits.this);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), OutfitClasificado.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Theme", item);
                    //categoria se basa en si es uso (1), color/es (2)
                    intent.putExtra("Categoria", categoria);
                    intent.putExtra("id_viaje", id_viaje);
                    intent.putExtra("dia",diaSeleccionado);
                    startActivity(intent);
                }

            }
        });
        //presionas elemento para borrar
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> extras=BD.getUsoExtra();
                String item=(String) adapterView.getItemAtPosition(i);
                if(extras.contains(item))
                    ventanaEmergenteEliminar(Outfits.this,item);
                return true;
            }
        });
        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diaSeleccionado!=null){
                    Intent intent = new Intent(getApplicationContext(), PantallaCalendario.class);
                    intent.putExtra("username", username);
                    intent.putExtra("dia", diaSeleccionado);
                    startActivity(intent);
                }
                else if(id_viaje==0) {
                    Intent intent = new Intent(getApplicationContext(), RopaOOutfits.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ViajeGuardado.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id_viaje", id_viaje);
                    startActivity(intent);
                }
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
    private void ventanaEmergenteAniadir(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ingrese el nuevo uso");
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(layoutParams);
        builder.setView(input);
        builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtenemos el texto ingresado por el usuario
                String enteredName = input.getText().toString();
                Toast.makeText(Outfits.this, "Nuevo uso: " + enteredName, Toast.LENGTH_SHORT).show();

                elementos.add(enteredName);
                BD.añadirUsoExtra(enteredName);
                adapter= new ListaAdapter(Outfits.this,elementos,null, false);
                listView.setAdapter(adapter);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario canceló la entrada de texto
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void ventanaEmergenteEliminar(Context context, String item){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Seguro que quieres eliminar este uso?");
        ArrayList<Integer> listaUnicosUso=BD.unicoUso(item);
        if(!listaUnicosUso.isEmpty() || !BDO.unicoUso(item).isEmpty() )
            builder.setMessage("Tienes prendas o conjuntos designados exclusivamente para '"+item+ "', y se eliminarán por completo.");
        builder.setCancelable(false).setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario canceló la entrada de texto
                dialog.cancel();
            }
        }).setNegativeButton("ELIMINAR "+item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Outfits.this, "Eliminado uso: " + item, Toast.LENGTH_SHORT).show();
                for(int id: listaUnicosUso){
                    //borramos las fotos relacionadas
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
                    islandRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Outfits.this, "BORRADO",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Outfits.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                        }
                    });
                    //borramos las combinaciones que contengan las prendas borradas
                    for(int ids:BDO.combinacionesConID(id)) {
                        BDO.eliminarOutfit(ids);
                        FirebaseStorage storage2 = FirebaseStorage.getInstance();
                        StorageReference storageRef2 = storage2.getReference();
                        StorageReference islandRef2 = storageRef2.child("outfits/" + username + "/" + ids);
                        islandRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Outfits.this, "BORRADO",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Outfits.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
                ArrayList<Integer> listaUnicosUsoOutfits=BDO.unicoUso(item);
                if(!listaUnicosUsoOutfits.isEmpty()){
                    for(int id: listaUnicosUsoOutfits){
                        BDO.eliminarOutfit(id);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference islandRef = storageRef.child("outfits/" + username + "/" + id);
                        islandRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Outfits.this, "BORRADO",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Outfits.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
                elementos.remove(item);
                BD.eliminarUsoExtra(item);
                ListaAdapter adapter= new ListaAdapter(Outfits.this,elementos,null, false);
                listView.setAdapter(adapter);
            }
        });
        builder.show();
    }
}
