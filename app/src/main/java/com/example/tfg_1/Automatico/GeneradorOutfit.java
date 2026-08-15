package com.example.tfg_1.Automatico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
import com.example.tfg_1.Automatico.AniadirC2;
import com.example.tfg_1.CrearManual.AniadirComplementos;
import com.example.tfg_1.CrearManual.InfoOutfit;
import com.example.tfg_1.ManualOAutomatic;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viajes.Seleccion_Outfits;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GeneradorOutfit extends AppCompatActivity {
    String username, uso, clima, estiloActual;
    int id_viaje,posLista, modo;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados, idsExtra;
    ProgressBar pbUp,pbDown,pbEntero,pbCalzado, pbextra1, pbextra2, pbextra3, uploading;
    ImageView imageViewUp, imageViewDown,imageViewEntero, imageCalzado, extra1, extra2,extra3;
    Button back,home,calendar, aceptar, rehacer;
    FloatingActionButton add1,add2,add3;
    BaseDatosPrendas BDP;
    BaseDatosOutfits BDO;
    StorageReference storageRef;
    List<Integer> calzado, abrigos, abajo_largo, jerse_sud,arriba_corto, abajo_corto,completes;
    int idUp,idDown,idEntero, idCalzado;
    LinearLayout estilos;
    HashMap<String,View> todosEstilos;
    final long ONE_MEGABYTE = 1024 * 1024;
    boolean desdeAniadir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generador_outfit);

        Intent i=getIntent();
        BDP=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);
        idsExtra=new ArrayList<>();
        idsExtra.add(0);
        idsExtra.add(0);
        idsExtra.add(0);
        estiloActual="Aleatorio";
        desdeAniadir=false;


        if(i!=null) {
            username = i.getStringExtra("username");
            uso= i.getStringExtra("uso");
            clima= i.getStringExtra("clima");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }

            if(i.getIntExtra("Calzado",0)!=0){
                //significa que se esta aniadiendo desde aniadirC3
                desdeAniadir=true;
                estiloActual=i.getStringExtra("style");
                if (i.getStringExtra("Tipo").equals("PorPartes")) {
                    modo=0;
                    idUp = i.getIntExtra("PArriba",0);
                    idDown = i.getIntExtra("PAbajo",0);
                }
                else {
                    modo=1;
                    idEntero = i.getIntExtra("Entero", 0);
                }
                idCalzado=i.getIntExtra("Calzado",0);
                idsExtra=i.getIntegerArrayListExtra("PExtra");

            }
        }
        todosEstilos=new HashMap<>();
        estilos=findViewById(R.id.styleContainer);
        imageViewUp=findViewById(R.id.imageViewUp);
        imageViewDown=findViewById(R.id.imageViewDown);
        imageCalzado=findViewById(R.id.imageViewCalzado);
        imageViewEntero=findViewById(R.id.imageViewEntero);
        extra1=findViewById(R.id.imageView4);
        extra2=findViewById(R.id.imageView5);
        extra3=findViewById(R.id.imageView10);
        pbUp=findViewById(R.id.progressBarUp2);
        pbDown=findViewById(R.id.progressBarDown2);
        pbCalzado=findViewById(R.id.progressBarCalzado3);
        pbEntero=findViewById(R.id.progressBarEntero);
        pbextra1=findViewById(R.id.progressBarextra);
        pbextra2=findViewById(R.id.progressBarextra4);
        pbextra3=findViewById(R.id.progressBarextra5);
        add1=findViewById(R.id.floatingActionButton);
        add2=findViewById(R.id.floatingActionButton4);
        add3=findViewById(R.id.floatingActionButton5);
        aceptar=findViewById(R.id.bttnAceptt);
        rehacer=findViewById(R.id.bttnOtro);
        uploading=findViewById(R.id.progressBar5);
        uploading.setVisibility(View.INVISIBLE);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        
        aniadir_eliminarExtras();
        visualizar_estilos();
        todosEstilos.get(estiloActual).setSelected(true);
        configurarPrendasAEstilo(estiloActual);


        
        if(desdeAniadir){
            //ponemos los recibidos
            formaGeneralizada(idUp,idDown,idEntero,idsExtra.get(0),idsExtra.get(1),idsExtra.get(2));
            ponerImagenEn(idCalzado, pbCalzado,imageCalzado, null);
            desdeAniadir=false;

        }
        else
            algoritmoGlobal();
        rehacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                algoritmoGlobal();
            }

        });

        //aceptar
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneradorOutfit.this);
                builder.setTitle("Ingrese el nombre de outfit");
                if(clima.endsWith("con"))
                    builder.setMessage("No se olvide de su Paraguas");
                final EditText input = new EditText(GeneradorOutfit.this);
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
                        guardarImagen(enteredName);
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
        });
        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posLista==-1) {
                    Intent intent = new Intent(getApplicationContext(), AutomaticoUso.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id_viaje", id_viaje);
                    intent.putExtra("vez_estancados", posLista);
                    intent.putExtra("clima", clima);
                    intent.putExtra("veces", veces_todos);
                    intent.putExtra("outfits_anteriores", idsSelecionados);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ManualOAutomatic.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id_viaje", id_viaje);
                    intent.putExtra("vez_estancados", posLista);
                    intent.putExtra("veces", veces_todos);
                    intent.putExtra("outfits_anteriores", idsSelecionados);
                    startActivity(intent);
                }
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
    private  void algoritmoGlobal(){
        if(clima.startsWith("Primavera"))
            algoritmoPrimavera();
        else if(clima.startsWith("Verano"))
            algoritmoVerano();
        else if(clima.startsWith("Otoño"))
            algoritmoOtonio();
        else
            algoritmoInvierno();
    }
    private void algoritmoPrimavera() {
        //donde 0 es por partes y 1 es entero
        if (completes.isEmpty())
            //solo de la forma por partes
            modo = 0;

        else if (abajo_largo.isEmpty() || arriba_corto.isEmpty())
            //solo de la forma entero
            modo = 1;

        else
            //ambas son posible
            modo = numeroRandom(2);

        int c2=0;
        if(clima.endsWith("con"))//llueve
            c2=abrigos.get(numeroRandom(abrigos.size()));

        if (modo == 0)
            formaGeneralizada(arriba_corto.get(numeroRandom(arriba_corto.size())),abajo_largo.get(numeroRandom(abajo_largo.size())),0,jerse_sud.get(numeroRandom(jerse_sud.size())),c2,0);
        else
            formaGeneralizada(0,0, completes.get(numeroRandom(completes.size())), jerse_sud.get(numeroRandom(jerse_sud.size())), c2, 0);

    }

    private void algoritmoVerano() {
        //donde 0 es por partes y 1 es entero
        if (completes.isEmpty())
            //solo de la forma por partes
            modo = 0;

        else if (abajo_corto.isEmpty() || arriba_corto.isEmpty())
            //solo de la forma entero
            modo = 1;

        else
            //ambas son posible
            modo = numeroRandom(2);

        int c1=0;
        if(clima.endsWith("con"))//llueve
            c1=abrigos.get(numeroRandom(abrigos.size()));
        else if(clima.endsWith("floja"))//llueve poco
            c1=jerse_sud.get(numeroRandom(jerse_sud.size()));

        if (modo == 0)
            formaGeneralizada(arriba_corto.get(numeroRandom(arriba_corto.size())), abajo_corto.get(numeroRandom(abajo_corto.size())),0,c1,0,0);
        else
            formaGeneralizada(0,0, completes.get(numeroRandom(completes.size())), c1, 0, 0);


    }
    private void algoritmoOtonio(){
        //donde 0 es por partes y 1 es entero
        if (completes.isEmpty())
            //solo de la forma por partes
            modo = 0;

        else if (abajo_largo.isEmpty() || arriba_corto.isEmpty())
            //solo de la forma entero
            modo = 1;

        else
            //ambas son posible
            modo = numeroRandom(2);

        if (modo == 0)
            formaGeneralizada(jerse_sud.get(numeroRandom(jerse_sud.size())), abajo_largo.get(numeroRandom(abajo_largo.size())), 0, abrigos.get(numeroRandom(abrigos.size())), arriba_corto.get(numeroRandom(arriba_corto.size())),0);
        else
            formaGeneralizada(0,0, completes.get(numeroRandom(completes.size())), abrigos.get(numeroRandom(abrigos.size())), jerse_sud.get(numeroRandom(jerse_sud.size())), 0);

    }

    private void algoritmoInvierno(){

        //donde 0 es por partes y 1 es entero
        if (completes.isEmpty())
            //solo de la forma por partes
            modo = 0;

        else if (abajo_largo.isEmpty() || arriba_corto.isEmpty())
            //solo de la forma entero
            modo = 1;

        else
            //ambas son posible
            modo = numeroRandom(2);

        if (modo == 0)
            formaGeneralizada(abrigos.get(numeroRandom(abrigos.size())), abajo_largo.get(numeroRandom(abajo_largo.size())), 0, jerse_sud.get(numeroRandom(jerse_sud.size())), arriba_corto.get(numeroRandom(arriba_corto.size())), 0);
        else
            formaGeneralizada(abrigos.get(numeroRandom(abrigos.size())), completes.get(numeroRandom(completes.size())), 0, jerse_sud.get(numeroRandom(jerse_sud.size())), 0, 0);


    }
    private int numeroRandom(int max){
        Random random= new Random();
        return random.nextInt(max);
    }
    private void formaGeneralizada(int up, int down, int complete, int c1, int c2, int c3){
        restaurarExtras(3);
        if(complete==0) {
            imageViewEntero.setVisibility(View.INVISIBLE);
            pbEntero.setVisibility(View.INVISIBLE);
            pbUp.setVisibility(View.VISIBLE);
            pbDown.setVisibility(View.VISIBLE);
            idUp=ponerImagenEn(up, pbUp,imageViewUp,null);
            idDown=ponerImagenEn(down, pbDown,imageViewDown,null);
            idEntero=0;
        }
        else{
            imageViewUp.setVisibility(View.INVISIBLE);
            imageViewDown.setVisibility(View.INVISIBLE);
            pbEntero.setVisibility(View.VISIBLE);
            pbUp.setVisibility(View.INVISIBLE);
            pbDown.setVisibility(View.INVISIBLE);
            idUp=0;
            idDown=0;
            idEntero=ponerImagenEn(complete, pbEntero,imageViewEntero,null);
        }
        pbCalzado.setVisibility(View.VISIBLE);
        if(!desdeAniadir)
            idCalzado=ponerImagenEn(calzado.get(numeroRandom(calzado.size())), pbCalzado,imageCalzado, null);

        pbextra1.setVisibility(View.INVISIBLE);
        pbextra2.setVisibility(View.INVISIBLE);
        pbextra3.setVisibility(View.INVISIBLE);
        if(c1!=0){
            pbextra1.setVisibility(View.VISIBLE);
            idsExtra.remove(0);
            idsExtra.add(0,ponerImagenEn(c1,pbextra1,extra1,add1));
        }
        if(c2!=0){
            pbextra2.setVisibility(View.VISIBLE);
            idsExtra.remove(1);
            idsExtra.add(1,ponerImagenEn(c2,pbextra2,extra2,add2));
        }
        if(c3!=0){
            pbextra3.setVisibility(View.VISIBLE);
            idsExtra.remove(2);
            idsExtra.add(2,ponerImagenEn(c3,pbextra3,extra3,add3));
        }

    }
    private int ponerImagenEn(int id, ProgressBar pb, ImageView iv, FloatingActionButton extra){
        if(extra!=null){
            extra.setEnabled(false);
            extra.setVisibility(View.GONE);
        }
        StorageReference islandRef = storageRef.child("prendas/" + username + "/" + id);
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pb.setVisibility(View.INVISIBLE);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageBitmap(bm);
                iv.setVisibility(View.VISIBLE);
            }
        });
        return id;
    }
    private void aniadir_eliminarExtras(){
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(0);
            }
        });
        extra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(0);
            }
        });
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(1);
            }
        });
        extra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(1);
            }
        });
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(2);
            }
        });
        extra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImagen(2);
            }
        });
        extra1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                restaurarExtras(0);
                return true;
            }
        });
        extra2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                restaurarExtras(1);
                return true;
            }
        });
        extra3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                restaurarExtras(2);
                return true;
            }
        });
    }
    private void restaurarExtras(int pos){
        if(pos==0 || pos==3){
            idsExtra.remove(0);
            idsExtra.add(0,0);
            extra1.setImageResource(R.drawable.edit_text_rectangular_background);
            add1.setEnabled(true);
            add1.setVisibility(View.VISIBLE);
        }
        if(pos==1 || pos==3){
            idsExtra.remove(1);
            idsExtra.add(1,0);
            extra2.setImageResource(R.drawable.edit_text_rectangular_background);
            add2.setEnabled(true);
            add2.setVisibility(View.VISIBLE);
        }
        if(pos==2 || pos==3){
            idsExtra.remove(2);
            idsExtra.add(2,0);
            extra3.setImageResource(R.drawable.edit_text_rectangular_background);
            add3.setEnabled(true);
            add3.setVisibility(View.VISIBLE);
        }
    }
    private void visualizar_estilos(){
        View styleChip= LayoutInflater.from(this).inflate(R.layout.style_chip,estilos,false);
        TextView tv=styleChip.findViewById(R.id.chipTextView);
        tv.setText("Aleatorio");
        estilos.addView(tv);
        todosEstilos.put("Aleatorio",styleChip);
        styleChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!estiloActual.equals("Aleatorio")) {
                    configurarPrendasAEstilo("Aleatorio");

                    //dejar seleccionado
                    todosEstilos.get(estiloActual).setSelected(false);
                    estiloActual = "Aleatorio";
                    todosEstilos.get(estiloActual).setSelected(true);

                    //crar outfit
                    algoritmoGlobal();
                }

            }
        });

        //Calidos
        ArrayList<String> calidos=new ArrayList<>();
        calidos.add("Rojo");
        calidos.add("Naranja");
        calidos.add("Amarillo");
        calidos.add("Verde Claro");
        calidos.add("Azul Claro");
        calidos.add("Rosa");
        calidos.add("Azul");
        if(posibilidadEstilo(calidos)){
            View styleChip5 = LayoutInflater.from(this).inflate(R.layout.style_chip_calido, estilos, false);
            TextView tv3 = styleChip5.findViewById(R.id.chipTextView);
            tv3.setText("Colores Cálidos");
            estilos.addView(tv3);
            todosEstilos.put("Colores Cálidos",styleChip5);
            styleChip5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("Colores Cálidos")) {
                        configurarPrendasAEstilo("Colores Cálidos");
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "Colores Cálidos";
                        todosEstilos.get(estiloActual).setSelected(true);
                        //crar outfit
                        algoritmoGlobal();
                    }
                }
            });
        }
        //Colores Apagados
        ArrayList<String> apagados=new ArrayList<>();
        apagados.add("Rojo");
        apagados.add("Verde Oscuro");
        apagados.add("Negro");
        apagados.add("Morado");

        if(posibilidadEstilo(apagados)){
            View styleChip7 = LayoutInflater.from(this).inflate(R.layout.style_chip_apagado, estilos, false);
            TextView tv3 = styleChip7.findViewById(R.id.chipTextView);
            tv3.setText("Colores Apagados");
            estilos.addView(tv3);
            todosEstilos.put("Colores Apagados",styleChip7);
            styleChip7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("Colores Apagados")) {
                        configurarPrendasAEstilo("Colores Apagados");
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "Colores Apagados";
                        todosEstilos.get(estiloActual).setSelected(true);
                        //crar outfit
                        algoritmoGlobal();
                    }
                }
            });
        }
        //Azules
        ArrayList<String> azules=new ArrayList<>();
        azules.add("Azul Claro");
        azules.add("Azul");
        azules.add("Azul Marino");
        azules.add("Blanco");
        if(posibilidadEstilo(azules)){
            View styleChip4 = LayoutInflater.from(this).inflate(R.layout.style_chip_blue, estilos, false);
            TextView tv3 = styleChip4.findViewById(R.id.chipTextView);
            tv3.setText("Azules");
            estilos.addView(tv3);
            todosEstilos.put("Azules",styleChip4);
            styleChip4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("Azules")) {
                        configurarPrendasAEstilo("Azules");
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "Azules";
                        todosEstilos.get(estiloActual).setSelected(true);
                        //crar outfit
                        algoritmoGlobal();
                    }
                }
            });
        }
        //Tonos terrosos
        ArrayList<String> terrosos=new ArrayList<>();
        terrosos.add("Blanco");
        terrosos.add("Marron");
        terrosos.add("Beige");

        if(posibilidadEstilo(terrosos)){
            View styleChip6 = LayoutInflater.from(this).inflate(R.layout.style_chip_terroso, estilos, false);
            TextView tv3 = styleChip6.findViewById(R.id.chipTextView);
            tv3.setText("Tonos Terrosos");
            estilos.addView(tv3);
            todosEstilos.put("Tonos Terrosos",styleChip6);
            styleChip6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("Tonos Terrosos")) {
                        configurarPrendasAEstilo("Tonos Terrosos");
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "Tonos Terrosos";
                        todosEstilos.get(estiloActual).setSelected(true);
                        //crar outfit
                        algoritmoGlobal();
                    }
                }
            });
        }

        //Colores Neutros
        ArrayList<String> blanco_negro_gris=new ArrayList<>();
        blanco_negro_gris.add("Negro");
        blanco_negro_gris.add("Gris");
        blanco_negro_gris.add("Blanco");
        if(posibilidadEstilo(blanco_negro_gris)){
            View styleChip3 = LayoutInflater.from(this).inflate(R.layout.style_chip_gris, estilos, false);
            TextView tv3 = styleChip3.findViewById(R.id.chipTextView);
            tv3.setText("Colores Neutros");
            estilos.addView(tv3);
            todosEstilos.put("Colores Neutros",styleChip3);
            styleChip3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("Colores Neutros")) {
                        configurarPrendasAEstilo("Colores Neutros");
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "Colores Neutros";
                        todosEstilos.get(estiloActual).setSelected(true);
                        //crar outfit
                        algoritmoGlobal();
                    }
                }
            });
        }

        ArrayList<String> negro=new ArrayList<>();
        negro.add("Negro");
        //ALL BLACK
        if(posibilidadEstilo(negro)) {
            //si se puede un all black
            View styleChip2 = LayoutInflater.from(this).inflate(R.layout.style_chip_black, estilos, false);
            TextView tv2 = styleChip2.findViewById(R.id.chipTextView);
            tv2.setText("All Black");
            estilos.addView(tv2);
            todosEstilos.put("All Black",styleChip2);
            styleChip2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!estiloActual.equals("All Black")) {
                        configurarPrendasAEstilo("All Black");

                        tv2.setSelected(true);
                        todosEstilos.get(estiloActual).setSelected(false);
                        estiloActual = "All Black";

                        //crar outfit
                        algoritmoGlobal();
                    }
                    }
                });
            }

    }

    /**
     * @param estilo
     * Cambia los valores de las prendas segun su estilo
     */
    private void configurarPrendasAEstilo(String estilo) {
        ArrayList<String> colores=new ArrayList<>();
        if(estilo.equals("All Black")){
            colores.add("Negro");
        }
        else if(estilo.equals("Colores Neutros")){
            colores.add("Negro");
            colores.add("Gris");
            colores.add("Blanco");
        } else if(estilo.equals("Azules")){
            colores.add("Azul Claro");
            colores.add("Azul");
            colores.add("Azul Marino");
            colores.add("Blanco");
        } else if(estilo.equals("Colores Cálidos")){
            colores.add("Rojo");
            colores.add("Verde");
            colores.add("Amarillo");
            colores.add("Verde Claro");
            colores.add("Azul Claro");
            colores.add("Rosa");
            colores.add("Azul");
        }else if(estilo.equals("Tonos Terrosos")){
            colores.add("Blanco");
            colores.add("Marron");
            colores.add("Beige");
        }else if(estilo.equals("Colores Apagados")){
            colores.add("Rojo");
            colores.add("Verde Oscuro");
            colores.add("Negro");
            colores.add("Morado");
        }


        if(estilo.equals("Aleatorio")) {
            calzado=BDP.pedirTodasFotosTipoUso("Calzados",uso);
            abrigos=BDP.pedirTodasFotosTipoUso("Abrigos",uso);
            abajo_largo= BDP.pedirTodasFotosTipoUso("Pantalones Largos",uso);
            abajo_largo.addAll(BDP.pedirTodasFotosTipoUso("Faldas",uso));
            abajo_corto= BDP.pedirTodasFotosTipoUso("Pantalones Cortos",uso);
            abajo_corto.addAll(BDP.pedirTodasFotosTipoUso("Faldas",uso));
            abajo_corto.addAll(BDP.pedirTodasFotosTipoUso("Bañadores",uso));
            jerse_sud= BDP.pedirTodasFotosTipoUso("Jerseis",uso);
            jerse_sud.addAll(BDP.pedirTodasFotosTipoUso("Sudaderas",uso));
            arriba_corto= BDP.pedirTodasFotosTipoUso("Camisetas",uso);
            arriba_corto.addAll(BDP.pedirTodasFotosTipoUso("Camisas/Blusas",uso));
            arriba_corto.addAll(BDP.pedirTodasFotosTipoUso("Polos",uso));
            completes=BDP.pedirTodasFotosTipoUso("Trajes",uso);
            completes.addAll(BDP.pedirTodasFotosTipoUso("Vestidos",uso));
            completes.addAll(BDP.pedirTodasFotosTipoUso("Monos",uso));
        }
        else{

            calzado = BDP.pedirTodasFotosTipoUsoColores("Calzados", uso, colores);
            abrigos = BDP.pedirTodasFotosTipoUsoColores("Abrigos", uso, colores);
            abajo_largo = BDP.pedirTodasFotosTipoUsoColores("Pantalones Largos", uso, colores);
            abajo_largo.addAll(BDP.pedirTodasFotosTipoUsoColores("Faldas", uso, colores));
            abajo_corto= BDP.pedirTodasFotosTipoUsoColores("Pantalones Cortos",uso,colores);
            abajo_corto.addAll(BDP.pedirTodasFotosTipoUsoColores("Faldas",uso,colores));
            abajo_corto.addAll(BDP.pedirTodasFotosTipoUsoColores("Bañadores",uso,colores));
            jerse_sud = BDP.pedirTodasFotosTipoUsoColores("Jerseis", uso, colores);
            jerse_sud.addAll(BDP.pedirTodasFotosTipoUsoColores("Sudaderas", uso, colores));
            arriba_corto = BDP.pedirTodasFotosTipoUsoColores("Camisetas", uso, colores);
            arriba_corto.addAll(BDP.pedirTodasFotosTipoUsoColores("Camisas/Blusas", uso, colores));
            arriba_corto.addAll(BDP.pedirTodasFotosTipoUsoColores("Polos", uso, colores));
            completes = BDP.pedirTodasFotosTipoUsoColores("Trajes", uso, colores);
            completes.addAll(BDP.pedirTodasFotosTipoUsoColores("Vestidos", uso, colores));
            completes.addAll(BDP.pedirTodasFotosTipoUsoColores("Monos", uso, colores));
        }
    }

    /**
     * @param colores Lista de colores a preguntar
     * @return true if se podria hacer un outfit con esos colores en esa estacion
     */
    private boolean posibilidadEstilo(ArrayList<String> colores) {
        boolean partesArribaCortas=(!BDP.pedirTodasFotosTipoUsoColores("Camisetas",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Camisas/Blusas",uso,colores).isEmpty() ||!BDP.pedirTodasFotosTipoUsoColores("Polos",uso,colores).isEmpty());
        boolean partesAbajoCortas=(!BDP.pedirTodasFotosTipoUsoColores("Pantalones Cortos",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Bañadores",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Faldas",uso,colores).isEmpty());
        boolean partesAbajoLargas=(!BDP.pedirTodasFotosTipoUsoColores("Pantalones Largos",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Faldas",uso,colores).isEmpty());
        boolean partesCompletas=(!BDP.pedirTodasFotosTipoUsoColores("Vestidos",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Monos",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Trajes",uso,colores).isEmpty());
        boolean partesAbrigoMedio=(!BDP.pedirTodasFotosTipoUsoColores("Jerseis",uso,colores).isEmpty() || !BDP.pedirTodasFotosTipoUsoColores("Sudaderas",uso,colores).isEmpty());
        boolean abrigo=(!BDP.pedirTodasFotosTipoUsoColores("Abrigos",uso,colores).isEmpty());

        if(BDP.pedirTodasFotosTipoUsoColores("Calzados",uso,colores).isEmpty())
            return false;
        else if(clima.equals("Verano")){
            if((clima.equals("Verano con") && !abrigo) || (clima.equals("Verano floja") && !partesAbrigoMedio) || (!partesCompletas && (!partesArribaCortas || !partesAbajoCortas)))
                return false;
            else
                return true;
        }
        else if(clima.startsWith("Primavera")){
            if((clima.equals("Primavera con") && !abrigo) || !partesAbrigoMedio || (!partesCompletas && (!partesArribaCortas || !partesAbajoLargas)))
                return false;
            else
                return true;
        }
        else{
            //otoño e invierno
            if(!abrigo || !partesAbrigoMedio || (!partesCompletas && (!partesArribaCortas || !partesAbajoLargas)))
                return false;
            else
                return true;
        }
    }

    private void addImagen(int pos){
        Intent intent =new Intent(GeneradorOutfit.this, AniadirC2.class);
        intent.putExtra("username",username);
        if(modo==0 || clima.startsWith("Invierno")) {
            intent.putExtra("Tipo", "PorPartes");
            intent.putExtra("PArriba",idUp);
            intent.putExtra("PAbajo", idDown);
        }
        else{
            intent.putExtra("Tipo", "Entero");
            intent.putExtra("Entero", idEntero);

        }
        intent.putExtra("Calzado", idCalzado);
        intent.putExtra("PExtra",idsExtra);
        intent.putExtra("pos",pos);
        intent.putExtra("uso",uso);
        intent.putExtra("clima",clima);
        intent.putExtra("style",estiloActual);
        if(id_viaje!=-1){
            intent.putExtra("id_viaje",id_viaje);
            intent.putExtra("veces",veces_todos);
            intent.putExtra("vez_estancados",posLista);
            intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
        }
        startActivity(intent);
    }
    private void guardarImagen (String name){
        aceptar.setEnabled(false);
        //guardar imagen de outfit
        add1.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);

        ArrayList<String> colores= todosColores();
        ArrayList<String> usos= todosUsos();

        //guardar datos en bbdd (id generado de outfit, ids prendas utilizadas por orden, colores, usos)
        int id=BDO.introducirOutfit(name,idUp,idDown,idEntero,idCalzado,idsExtra,colores,usos);
        if(id!=-1) {
            FrameLayout frameLayout = findViewById(R.id.frameLayout);
            Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            frameLayout.draw(canvas);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference userRef = storageRef.child("outfits/" + username);
            StorageReference idRef = userRef.child(String.valueOf(id));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask= idRef.putBytes(data);
            uploading.setVisibility(View.VISIBLE);

            //llevar a info_outfit
            uploadTask.addOnCompleteListener(GeneradorOutfit.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    uploading.setVisibility(View.GONE);
                    aceptar.setEnabled(true);
                    if(id_viaje==-1) {
                        Intent intent = new Intent(GeneradorOutfit.this, InfoOutfit.class);
                        intent.putExtra("username", username);
                        intent.putExtra("id", id);
                        intent.putExtra("Theme", usos.get(0));
                        intent.putExtra("Categoria", 1);
                        startActivity(intent);
                    }
                    else if(posLista==-1){
                        ArrayList<Integer> aux=new ArrayList<>();
                        aux.add(id);
                        BDO.editarOutfitsViajes(id_viaje,aux);
                        Intent intent = new Intent(GeneradorOutfit.this, ViajeGuardado.class);
                        intent.putExtra("username", username);
                        intent.putExtra("id_viaje", id_viaje);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(GeneradorOutfit.this, Seleccion_Outfits.class);
                        intent.putExtra("username",username);
                        intent.putExtra("id_viaje",id_viaje);
                        intent.putExtra("veces", veces_todos);
                        intent.putExtra("vez_estancados", posLista);
                        intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
                        startActivity(intent);
                    }
                }
            });


        }
        else
            Toast.makeText(GeneradorOutfit.this,"Tenemos problemas con la BBDD", Toast.LENGTH_SHORT).show();
    }
    private ArrayList<String> todosColores(){
        ArrayList<String> res=new ArrayList<>();
        if(modo==0 || clima.startsWith("Invierno")) {
            for (String c : BDP.pedirColoresPrenda(idUp))
                if (!res.contains(c))
                    res.add(c);
            for (String c : BDP.pedirColoresPrenda(idDown))
                if (!res.contains(c))
                    res.add(c);
        }
        else{
            for(String c:BDP.pedirColoresPrenda(idEntero))
                if(!res.contains(c))
                    res.add(c);
        }

        for(String c:BDP.pedirColoresPrenda(idCalzado))
            if(!res.contains(c))
                res.add(c);
        if(!idsExtra.isEmpty()){
            if(idsExtra.get(0)!=0){
                for(String c:BDP.pedirColoresPrenda(idsExtra.get(0)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(idsExtra.get(1)!=0){
                for(String c:BDP.pedirColoresPrenda(idsExtra.get(1)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(idsExtra.get(2)!=0){
                for(String c:BDP.pedirColoresPrenda(idsExtra.get(2)))
                    if(!res.contains(c))
                        res.add(c);
            }
        }
        return res;
    }
    private ArrayList<String> todosUsos(){
        ArrayList<String> res=new ArrayList<>();
        if(modo==0 || clima.startsWith("Invierno")) {
            for (String c : BDP.pedirUsoPrenda(idUp))
                if (!res.contains(c))
                    res.add(c);
            for (String c : BDP.pedirUsoPrenda(idDown))
                if (!res.contains(c))
                    res.add(c);
        }
        else{
            for(String c:BDP.pedirUsoPrenda(idEntero))
                if(!res.contains(c))
                    res.add(c);
        }

        for(String c:BDP.pedirUsoPrenda(idCalzado))
            if(!res.contains(c))
                res.add(c);
        if(!idsExtra.isEmpty()){
            if(idsExtra.get(0)!=0){
                for(String c:BDP.pedirUsoPrenda(idsExtra.get(0)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(idsExtra.get(1)!=0){
                for(String c:BDP.pedirUsoPrenda(idsExtra.get(1)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(idsExtra.get(2)!=0){
                for(String c:BDP.pedirUsoPrenda(idsExtra.get(2)))
                    if(!res.contains(c))
                        res.add(c);
            }
        }
        return res;
    }
}
