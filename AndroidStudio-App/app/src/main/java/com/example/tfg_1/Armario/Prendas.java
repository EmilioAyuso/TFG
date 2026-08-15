package com.example.tfg_1.Armario;

import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Prendas extends AppCompatActivity {
    String username;
    RadioGroup radioGroup;
    ListView listView;
    Button back,home,calendar;
    int categoria;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    private List<String> elementos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prendas);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);

        radioGroup=findViewById(R.id.radioGroup);
        listView=findViewById(R.id.listView);

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
        categoria=1;

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            elementos.clear();
            if (i == R.id.radioTipo) {
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
                categoria=1;
                ArrayAdapter<String> adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,elementos);
                listView.setAdapter(adapter);

            } else if (i == R.id.radioColor) {

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
            else if(i==R.id.radioUso) {
                elementos.add("AÑADIR OTRO USO");
                for(String u:BD.getUsoExtra()){
                    elementos.add(u);
                }
                categoria=3;
                ListaAdapter adapter= new ListaAdapter(this,elementos,null, false);
                listView.setAdapter(adapter);
            }
        });
        Intent intent =getIntent();
        if(intent!=null)
            username=intent.getStringExtra("username");
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,elementos);
        listView.setAdapter(adapter);

        //Accionas a un elemento de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=(String) adapterView.getItemAtPosition(i);
                if(item.equals("AÑADIR OTRO USO")){
                    //añadir mas usos
                    ventanaEmergenteAniadir(Prendas.this);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), PrendaClasificada.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Theme", item);
                    //categoria se basa en si es tipo (1), color/es (2), uso (3)
                    intent.putExtra("Categoria", categoria);
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
                    ventanaEmergenteEliminar(Prendas.this,item);
                return true;
            }
        });

        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), RopaOOutfits.class);
                intent.putExtra("username",username);
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
                Toast.makeText(Prendas.this, "Nuevo uso: " + enteredName, Toast.LENGTH_SHORT).show();

                elementos.add(enteredName);
                BD.añadirUsoExtra(enteredName);
                ListaAdapter adapter= new ListaAdapter(Prendas.this,elementos,null, false);
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
                Toast.makeText(Prendas.this, "Eliminado uso: " + item, Toast.LENGTH_SHORT).show();
                for(int id: listaUnicosUso){
                    //borramos las fotos relacionadas
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
                    islandRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Prendas.this, "BORRADO",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Prendas.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

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
                                Toast.makeText(Prendas.this, "BORRADO",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Prendas.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

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
                                Toast.makeText(Prendas.this, "BORRADO",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Prendas.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
                elementos.remove(item);
                BD.eliminarUsoExtra(item);
                ListaAdapter adapter= new ListaAdapter(Prendas.this,elementos,null, false);
                listView.setAdapter(adapter);
            }
        });
        builder.show();
    }
}
