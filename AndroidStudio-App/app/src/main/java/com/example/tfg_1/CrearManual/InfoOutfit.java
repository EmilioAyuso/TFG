package com.example.tfg_1.CrearManual;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg_1.Armario.CameraActivity;
import com.example.tfg_1.Armario.GaleryActivity;
import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.Outfit;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viajes.MisViajes;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InfoOutfit extends AppCompatActivity {

    final long ONE_MEGABYTE = 1024 * 1024;
    BaseDatosOutfits BD;
    String username,theme;
    int id, categoria,viaje;
    TextView nombre;
    ImageView foto;
    FloatingActionButton basura;
    ProgressBar progressBar;
    LinearLayout etiquetasColores, etiquetasUsos, fotosExtra;
    ToggleButton parahoy;
    LocalDate hoy;
    Button back,home,calendar, editar, aniadir;
    Bitmap fotoP;
    String diaSeleccionado;
    ArrayList<String> usos, coloresOrdenados;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_outfit);
        BD=new BaseDatosOutfits(this);
        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            id=intent.getIntExtra("id",0);
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
            viaje=intent.getIntExtra("viaje",0);
            diaSeleccionado=intent.getStringExtra("dia");
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                diaSeleccionado=intent.getParcelableExtra("dia", LocalDate.class);
            }
             */
        }
        nombre=findViewById(R.id.nombrePrenda);
        progressBar=findViewById(R.id.progressBar2);
        foto=findViewById(R.id.imagenPrenda);
        fotosExtra=findViewById(R.id.fotsContainer);
        aniadir=findViewById(R.id.buttonAniadirImagenes);
        etiquetasColores=findViewById(R.id.colorContainer);
        etiquetasUsos=findViewById(R.id.useContainer);
        basura=findViewById(R.id.floatingActionBasura);
        parahoy=findViewById(R.id.toggleButtonlavando);
        editar=findViewById(R.id.buttonEditar);

        nombre.setText(BD.pedirNamePrenda(id));
        parahoy.setTextOn("Outfit de hoy");
        parahoy.setTextOff("Seleccionar para hoy");
        parahoy.setChecked(false);

        progressBar.setVisibility(View.VISIBLE);
        foto.setEnabled(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("outfits/" + username + "/" + id);
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                progressBar.setVisibility(View.GONE);
                foto.setEnabled(true);
                fotoP= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                foto.setImageBitmap(fotoP);
            }
        });

        añadirFotos(fotosExtra);
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirNuevaImagen();
            }
        });

        //etiquetas de colores
        coloresOrdenados=ordenarColores(BD.pedirColoresOutfit(id));
        List<Integer> coloresreales=transformarAColores(coloresOrdenados);
        for(int i=0;i<coloresreales.size();i++){
            agregarEtiquetaColor(coloresreales.get(i), coloresOrdenados.get(i),etiquetasColores);
        }
        //etiquetas de uso
        usos=BD.pedirUsoOutfit(id);
        for(String u:usos){
            agregarEtiquetaUso(u,etiquetasUsos);
        }
        //parahoy
        hoy= LocalDate.now();
        //cambiar estado lavado
        if(BD.idOutfit(String.valueOf(hoy))==id){
            parahoy.setChecked(true);
        }
        else
            parahoy.setChecked(false);
        //falta una mecanica para guardar en BBDD el elegido del dia
        parahoy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!parahoy.isChecked()){
                    //se deselecciona y elimina en bbdd calendario
                    BD.removeDayOutfit(String.valueOf(hoy));
                }
                else{
                    //cuando no este seleccionado

                    //se comprueba si en esta fecha hay viaje
                    if(BD.idViaje(String.valueOf(hoy))==0){
                        //si no, se pone este en bbdd calendario, eliminado anterior si lo hubiese
                        BD.removeDayOutfit(String.valueOf(hoy));
                        BD.addDayOutfit(String.valueOf(hoy),id);
                    }
                    else {
                        parahoy.setChecked(false);
                        Toast.makeText(InfoOutfit.this, "No puedes seleccionar Oufit del dia, estando de Viaje", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //editar
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder elegir= new AlertDialog.Builder(InfoOutfit.this);
                elegir.setCancelable(true).setPositiveButton("Características", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(InfoOutfit.this,ListaOpcionesOutfit.class);
                        intent.putExtra("username",username);
                        intent.putExtra("id",id);
                        intent.putExtra("Theme",theme);
                        intent.putExtra("Categoria",categoria);
                        intent.putExtra("ListaColores",coloresOrdenados);
                        intent.putExtra("ListaUsos",usos);
                        startActivity(intent);
                    }
                }).setNegativeButton("Outfit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(InfoOutfit.this,AniadirComplementos.class);
                        intent.putExtra("username",username);
                        Outfit of=BD.getOutfit(id);
                        if(of.getIdEntero()==0){
                            //por partes
                            intent.putExtra("Tipo","PorPartes");
                            intent.putExtra("PArriba",of.getIdUp());
                            intent.putExtra("PAbajo",of.getIdDown());
                        }
                        else{
                            intent.putExtra("Tipo","Entero");
                            intent.putExtra("Entero",of.getIdEntero());
                        }
                        intent.putExtra("Calzado",of.getIdCalzado());
                        intent.putExtra("PExtra",of.getIdsExtra());
                        intent.putExtra("id",id);
                        intent.putExtra("Theme",theme);
                        intent.putExtra("Categoria",categoria);
                        startActivity(intent);
                    }
                });
                AlertDialog titulo= elegir.create();
                titulo.setTitle("¿Quieres editar el Outfit o sus Características?:");
                titulo.show();
            }
        });
        //borrar la prenda
        basura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder elegir= new AlertDialog.Builder(InfoOutfit.this);
                elegir.setCancelable(false).setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("BORRAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        islandRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(InfoOutfit.this, "BORRADO",Toast.LENGTH_LONG).show();
                                BD.eliminarOutfit(id);
                                if(viaje==0) {
                                    Intent intent = new Intent(getApplicationContext(), OutfitClasificado.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("Theme", theme);
                                    intent.putExtra("Categoria", categoria);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent;
                                    if(BD.getViaje(viaje)==null){
                                        //se ha borrado el viaje entero
                                        intent = new Intent(getApplicationContext(), MisViajes.class);
                                    }
                                    else {
                                        intent = new Intent(getApplicationContext(), ViajeGuardado.class);
                                        intent.putExtra("id_viaje", viaje);
                                    }
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoOutfit.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
                AlertDialog titulo= elegir.create();
                titulo.setTitle("¿Seguro que quieres borrar este outfit?:");
                titulo.show();
            }
        });


        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viaje!=0) {
                    Intent intent = new Intent(getApplicationContext(), ViajeGuardado.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id_viaje", viaje);
                    startActivity(intent);
                }
                else if(diaSeleccionado!=null){
                    Intent intent = new Intent(getApplicationContext(), PantallaCalendario.class);
                    intent.putExtra("username", username);
                    intent.putExtra("dia",diaSeleccionado);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), OutfitClasificado.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Theme", theme);
                    intent.putExtra("Categoria", categoria);
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
    private void añadirFotos(LinearLayout container){
        //accedemos a las fotos en bbdd
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child("outfits/" + username + "/fotos_reales/" + id);
        islandRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()){
                    for(StorageReference item: task.getResult().getItems()){
                        item.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                ImageView imageView= new ImageView(InfoOutfit.this);
                                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        eliminarFoto(item);
                                        return true;
                                    }
                                });
                                imageView.setImageBitmap(bm);
                                fotosExtra.addView(imageView);
                            }
                        });
                    }
                }
            }
        });
    }
    private void añadirNuevaImagen(){
        AlertDialog.Builder elegir= new AlertDialog.Builder(InfoOutfit.this);
        elegir.setCancelable(true).setPositiveButton("Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent= new Intent(InfoOutfit.this, GaleryActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        }).setNegativeButton("Cámara", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent= new Intent(InfoOutfit.this, CameraActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        AlertDialog titulo= elegir.create();
        titulo.setTitle("Añadir Imagen desde:");
        titulo.show();
    }
    private void eliminarFoto(StorageReference item){
        item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fotosExtra.removeAllViews();
                ImageView imageView= new ImageView(InfoOutfit.this);
                imageView.setImageBitmap(fotoP);
                fotosExtra.addView(imageView);
                añadirFotos(fotosExtra);
            }
        });
    }
    /**
     * @param color etiqueta de color a añadir
     * @param container Container donde la añadiremos
     */
    private void agregarEtiquetaColor(int color,String c, LinearLayout container){
        View colorChip= LayoutInflater.from(this).inflate(R.layout.color_chip,container,false);
        if(color!=-1)
            colorChip.setBackgroundColor(color);

        container.addView(colorChip);
        colorChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),OutfitClasificado.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",c);
                intent.putExtra("Categoria",2);
                startActivity(intent);
            }
        });
    }

    /**
     * @param uso etiqueta de uso a añadir
     * @param container Container donde la añadiremos
     */
    private void agregarEtiquetaUso(String uso, LinearLayout container){
        View useChip= LayoutInflater.from(this).inflate(R.layout.use_chip,container,false);
        TextView chiptext=useChip.findViewById(R.id.chipTextView);
        chiptext.setText(uso);
        container.addView(useChip);
        chiptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),OutfitClasificado.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",uso);
                intent.putExtra("Categoria",1);
                startActivity(intent);
            }
        });
    }
    private ArrayList<String> ordenarColores(List<String> textColors) {
        ArrayList<String> res=new ArrayList<>();
        if(textColors.contains("Rojo"))
            res.add("Rojo");
        if(textColors.contains("Naranja"))
            res.add("Naranja");
        if(textColors.contains("Amarillo"))
            res.add("Amarillo");
        if(textColors.contains("Verde Claro"))
            res.add("Verde Claro");
        if(textColors.contains("Verde Oscuro"))
            res.add("Verde Oscuro");
        if(textColors.contains("Azul Claro"))
            res.add("Azul Claro");
        if(textColors.contains("Azul"))
            res.add("Azul");
        if(textColors.contains("Azul Marino"))
            res.add("Azul Marino");
        if(textColors.contains("Morado"))
            res.add("Morado");
        if(textColors.contains("Rosa"))
            res.add("Rosa");
        if(textColors.contains("Beige"))
            res.add("Beige");
        if(textColors.contains("Marron"))
            res.add("Marron");
        if(textColors.contains("Gris"))
            res.add("Gris");
        if(textColors.contains("Blanco"))
            res.add("Blanco");
        if(textColors.contains("Negro"))
            res.add("Negro");
        return res;
    }

    /**
     * Tranforma los colores mencionado ("Azul") a su correspondiente (#3d4cf5)
     * @param textColors es el String color
     * @return lista con colores de la prenda
     */
    private List<Integer> transformarAColores(List<String> textColors){
        List<Integer> res=new ArrayList<>();
        if(textColors.contains("Rojo"))
            res.add(ContextCompat.getColor(this,R.color.rojo));
        if(textColors.contains("Naranja"))
            res.add(ContextCompat.getColor(this,R.color.naranja));
        if(textColors.contains("Amarillo"))
            res.add(ContextCompat.getColor(this,R.color.amarillo));
        if(textColors.contains("Verde Claro"))
            res.add(ContextCompat.getColor(this,R.color.verde_claro));
        if(textColors.contains("Verde Oscuro"))
            res.add(ContextCompat.getColor(this,R.color.verde_oscuro));
        if(textColors.contains("Azul Claro"))
            res.add(ContextCompat.getColor(this,R.color.azul_claro));
        if(textColors.contains("Azul"))
            res.add(ContextCompat.getColor(this,R.color.azul));
        if(textColors.contains("Azul Marino"))
            res.add(ContextCompat.getColor(this,R.color.azul_marino));
        if(textColors.contains("Morado"))
            res.add(ContextCompat.getColor(this,R.color.morado));
        if(textColors.contains("Rosa"))
            res.add(ContextCompat.getColor(this,R.color.rosa));
        if(textColors.contains("Beige"))
            res.add(ContextCompat.getColor(this,R.color.beige));
        if(textColors.contains("Marron"))
            res.add(ContextCompat.getColor(this,R.color.marron));
        if(textColors.contains("Gris"))
            res.add(ContextCompat.getColor(this,R.color.gris));
        if(textColors.contains("Blanco"))
            res.add(ContextCompat.getColor(this,R.color.white));
        if(textColors.contains("Negro"))
            res.add(ContextCompat.getColor(this,R.color.black));
        return res;
    }

}
