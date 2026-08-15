package com.example.tfg_1.Armario;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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

import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class InfoPrenda extends AppCompatActivity {
    String username,theme;
    int id, categoria;
    TextView nombre;
    ImageView foto;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    FloatingActionButton basura;
    ProgressBar progressBar;
    LinearLayout etiquetasColores, etiquetasUsos;
    ToggleButton lavando;
    Button back,home,calendar, editar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_prenda);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);
        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            id=intent.getIntExtra("id",0);
            theme=intent.getStringExtra("Theme");
            categoria=intent.getIntExtra("Categoria",0);
        }
        nombre=findViewById(R.id.nombrePrenda);
        progressBar=findViewById(R.id.progressBar2);
        foto=findViewById(R.id.imagenPrenda);
        etiquetasColores=findViewById(R.id.colorContainer);
        etiquetasUsos=findViewById(R.id.useContainer);
        basura=findViewById(R.id.floatingActionBasura);
        lavando=findViewById(R.id.toggleButtonlavando);
        editar=findViewById(R.id.buttonEditar);

        nombre.setText(BD.pedirNamePrenda(username,id));

        //accedemos a la imagen de la prenda
        progressBar.setVisibility(View.VISIBLE);
        foto.setEnabled(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                progressBar.setVisibility(View.GONE);
                foto.setEnabled(true);
                Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                foto.setImageBitmap(bm);
            }
        });

        //etiquetas de colores
        ArrayList<String> coloresOrdenados=ordenarColores(BD.pedirColoresPrenda(id));
        List<Integer> coloresreales=transformarAColores(coloresOrdenados);
        for(int i=0;i<coloresreales.size();i++){
            agregarEtiquetaColor(coloresreales.get(i), coloresOrdenados.get(i),etiquetasColores);
        }
        //etiquetas de uso
        ArrayList<String> usos=BD.pedirUsoPrenda(id);
        for(String u:usos){
            agregarEtiquetaUso(u,etiquetasUsos);
        }
        //cambiar estado lavado
        if(BD.preguntaSeEstaLavando(id)){
            lavando.setChecked(false);
        }
        else
            lavando.setChecked(true);
        lavando.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BD.cambioLavado(id);
            }
        });

        //editar atributos de prenda
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),ListaOpciones.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
                intent.putExtra("imagen",Uri.parse("nulo"));
                intent.putExtra("Update",nombre.getText());

                intent.putStringArrayListExtra("ListaColores",coloresOrdenados);
                intent.putStringArrayListExtra("ListaUsos",usos);
                startActivity(intent);
            }
        });
        //borrar la prenda
        basura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder elegir= new AlertDialog.Builder(InfoPrenda.this);
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
                                Toast.makeText(InfoPrenda.this, "BORRADO",Toast.LENGTH_LONG).show();
                                BD.eliminar(id);
                                //borramos las combinaciones que incluyan esa prenda
                                for(int ids:BDO.combinacionesConID(id)) {
                                    BDO.eliminarOutfit(ids);
                                    FirebaseStorage storage2 = FirebaseStorage.getInstance();
                                    StorageReference storageRef2 = storage2.getReference();
                                    StorageReference islandRef2 = storageRef2.child("outfits/" + username + "/" + ids);
                                    islandRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(InfoPrenda.this, "BORRADO",Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(InfoPrenda.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }


                                Intent intent=new Intent(InfoPrenda.this, PrendaClasificada.class);
                                intent.putExtra("username",username);
                                intent.putExtra("Theme",theme);
                                intent.putExtra("Categoria",categoria);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoPrenda.this, "ERROR AL BORRAR",Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
                AlertDialog titulo= elegir.create();
                titulo.setTitle("¿Seguro que quieres borrar esta prenda?:");
                titulo.show();
            }
        });

        //Tabla de ayuda
        back=findViewById(R.id.BACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), PrendaClasificada.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",theme);
                intent.putExtra("Categoria",categoria);
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
                Intent intent= new Intent(getApplicationContext(),PrendaClasificada.class);
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
                Intent intent= new Intent(getApplicationContext(),PrendaClasificada.class);
                intent.putExtra("username",username);
                intent.putExtra("Theme",uso);
                intent.putExtra("Categoria",3);
                startActivity(intent);
            }
        });
    }
    private ArrayList<String> ordenarColores(List<String> textColors) {
        ArrayList<String> res=new ArrayList<>();
        //TODO: cambiar si añado mas colores
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
        //TODO: cambiar si añado mas colores
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
