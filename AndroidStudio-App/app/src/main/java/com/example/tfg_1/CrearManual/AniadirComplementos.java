package com.example.tfg_1.CrearManual;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.BaseDatosPrendas;
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

public class AniadirComplementos extends AppCompatActivity {
    String username, theme;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    boolean porPartes;
    int arriba,abajo,entero,calzado,id_editar, categoria;
    ArrayList<Integer> PrendasExtra;
    ProgressBar pbUp,pbDown,pbEntero,pbCalzado, pbextra1, pbextra2, pbextra3, uploading;
    ImageView imageViewUp, imageViewDown,imageViewEntero, imageCalzado, extra1, extra2,extra3;
    Button back,home,calendar,terminar;
    FloatingActionButton add1,add2,add3;
    BaseDatosPrendas BDP;
    BaseDatosOutfits BDO;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.aniadir_complementos);
        setContentView(R.layout.aniadir2);
        BDP=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);
        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            porPartes=false;
            if (i.getStringExtra("Tipo").equals("PorPartes")) {
                    porPartes=true;
                    arriba = i.getIntExtra("PArriba",0);
                    abajo = i.getIntExtra("PAbajo",0);
            }
            else
                entero = i.getIntExtra("Entero",0);
            calzado = i.getIntExtra("Calzado",0);
            PrendasExtra= i.getIntegerArrayListExtra("PExtra");
            id_editar=i.getIntExtra("id",0);
            if(id_editar!=0){
                theme=i.getStringExtra("Theme");
                categoria=i.getIntExtra("Categoria",0);
            }
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }

        }
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
        terminar=findViewById(R.id.bttnAceptt);
        uploading=findViewById(R.id.progressBar5);
        uploading.setVisibility(View.INVISIBLE);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);
        pbextra1.setVisibility(View.INVISIBLE);
        pbextra2.setVisibility(View.INVISIBLE);
        pbextra3.setVisibility(View.INVISIBLE);

        recuperarPrendasAnteriores(porPartes);

        terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_editar!=0)
                    editarImagen();

                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AniadirComplementos.this);
                    builder.setTitle("Ingrese el nombre de outfit");
                    final EditText input = new EditText(AniadirComplementos.this);
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
                            if(id_viaje!=-1 && posLista!=-1)
                                desdeViaje(enteredName);
                            else
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


            }
        });

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
                extra1.setImageResource(R.drawable.edit_text_rectangular_background);
                add1.setEnabled(true);
                add1.setVisibility(View.VISIBLE);
                PrendasExtra.remove(0);
                PrendasExtra.add(0,0);
                return true;
            }
        });
        extra2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                extra2.setImageResource(R.drawable.edit_text_rectangular_background);
                add2.setEnabled(true);
                add2.setVisibility(View.VISIBLE);
                PrendasExtra.remove(1);
                PrendasExtra.add(1,0);
                return true;
            }
        });
        extra3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                extra3.setImageResource(R.drawable.edit_text_rectangular_background);
                add3.setEnabled(true);
                add3.setVisibility(View.VISIBLE);
                PrendasExtra.remove(2);
                PrendasExtra.add(2,0);
                return true;
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
    private ArrayList<String> todosColores(){
        ArrayList<String> res=new ArrayList<>();
        if(porPartes) {
            for (String c : BDP.pedirColoresPrenda(arriba))
                if (!res.contains(c))
                    res.add(c);
            for (String c : BDP.pedirColoresPrenda(abajo))
                if (!res.contains(c))
                    res.add(c);
        }
        else{
            for(String c:BDP.pedirColoresPrenda(entero))
                if(!res.contains(c))
                    res.add(c);
        }

        for(String c:BDP.pedirColoresPrenda(calzado))
            if(!res.contains(c))
                res.add(c);
        if(!PrendasExtra.isEmpty()){
            if(PrendasExtra.get(0)!=0){
                for(String c:BDP.pedirColoresPrenda(PrendasExtra.get(0)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(PrendasExtra.get(1)!=0){
                for(String c:BDP.pedirColoresPrenda(PrendasExtra.get(1)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(PrendasExtra.get(2)!=0){
                for(String c:BDP.pedirColoresPrenda(PrendasExtra.get(2)))
                    if(!res.contains(c))
                        res.add(c);
            }
        }
        return res;
    }
    private ArrayList<String> todosUsos(){
        ArrayList<String> res=new ArrayList<>();
        if(porPartes) {
            for (String c : BDP.pedirUsoPrenda(arriba))
                if (!res.contains(c))
                    res.add(c);
            for (String c : BDP.pedirUsoPrenda(abajo))
                if (!res.contains(c))
                    res.add(c);
        }
        else{
            for(String c:BDP.pedirUsoPrenda(entero))
                if(!res.contains(c))
                    res.add(c);
        }

        for(String c:BDP.pedirUsoPrenda(calzado))
            if(!res.contains(c))
                res.add(c);
        if(!PrendasExtra.isEmpty()){
            if(PrendasExtra.get(0)!=0){
                for(String c:BDP.pedirUsoPrenda(PrendasExtra.get(0)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(PrendasExtra.get(1)!=0){
                for(String c:BDP.pedirUsoPrenda(PrendasExtra.get(1)))
                    if(!res.contains(c))
                        res.add(c);
            }
            if(PrendasExtra.get(2)!=0){
                for(String c:BDP.pedirUsoPrenda(PrendasExtra.get(2)))
                    if(!res.contains(c))
                        res.add(c);
            }
        }
        return res;
    }
    private void addImagen(int p){
        Intent intent =new Intent(AniadirComplementos.this,AniadirC2.class);
        intent.putExtra("username",username);
        if(porPartes) {
            intent.putExtra("Tipo", "PorPartes");
            intent.putExtra("PArriba", arriba);
            intent.putExtra("PAbajo", abajo);
        }
        else{
            intent.putExtra("Tipo", "Entero");
            intent.putExtra("Entero", entero);

        }
        intent.putExtra("Calzado", calzado);
        intent.putExtra("PExtra",PrendasExtra);
        intent.putExtra("pos",p);
        intent.putExtra("id",id_editar);
        intent.putExtra("Theme",theme);
        intent.putExtra("Categoria",categoria);
        if(id_viaje!=-1){
            intent.putExtra("id_viaje",id_viaje);
            intent.putExtra("veces",veces_todos);
            intent.putExtra("vez_estancados",posLista);
            intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
        }
        startActivity(intent);
    }

    private void recuperarPrendasAnteriores(boolean porPartes) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef;
        final long ONE_MEGABYTE = 1024 * 1024;
        if(porPartes){
            islandRef = storageRef.child("prendas/" + username + "/" + arriba);
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    pbUp.setVisibility(View.GONE);
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewUp.setImageBitmap(bm);
                }
            });
            islandRef = storageRef.child("prendas/" + username + "/" + abajo);
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    pbDown.setVisibility(View.GONE);
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewDown.setImageBitmap(bm);
                }
            });
            imageViewEntero.setVisibility(View.GONE);
            pbEntero.setVisibility(View.GONE);

        }
        else{
            islandRef = storageRef.child("prendas/" + username + "/" + entero);
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    pbEntero.setVisibility(View.GONE);
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageViewEntero.setImageBitmap(bm);
                }
            });
            imageViewUp.setVisibility(View.GONE);
            imageViewDown.setVisibility(View.GONE);
            pbUp.setVisibility(View.GONE);
            pbDown.setVisibility(View.GONE);

        }
        islandRef = storageRef.child("prendas/" + username + "/" + calzado);
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pbCalzado.setVisibility(View.GONE);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageCalzado.setImageBitmap(bm);
            }
        });
        if(!PrendasExtra.isEmpty()){
            if(PrendasExtra.get(0)!=0){
                add1.setEnabled(false);
                add1.setVisibility(View.GONE);
                pbextra1.setVisibility(View.VISIBLE);
                islandRef = storageRef.child("prendas/" + username + "/" + PrendasExtra.get(0));
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pbextra1.setVisibility(View.GONE);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        extra1.setImageBitmap(bm);
                    }
                });
            }
            if(PrendasExtra.get(1)!=0){
                add2.setEnabled(false);
                add2.setVisibility(View.GONE);
                pbextra2.setVisibility(View.VISIBLE);
                islandRef = storageRef.child("prendas/" + username + "/" + PrendasExtra.get(1));
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pbextra2.setVisibility(View.GONE);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        extra2.setImageBitmap(bm);
                    }
                });
            }
            if(PrendasExtra.get(2)!=0){
                add3.setEnabled(false);
                add3.setVisibility(View.GONE);
                pbextra3.setVisibility(View.VISIBLE);
                islandRef = storageRef.child("prendas/" + username + "/" + PrendasExtra.get(2));
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pbextra3.setVisibility(View.GONE);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        extra3.setImageBitmap(bm);
                    }
                });
            }
        }
    }
    private void guardarImagen (String name){
        terminar.setEnabled(false);
        //guardar imagen de outfit
        add1.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);

        ArrayList<String> colores= todosColores();
        ArrayList<String> usos= todosUsos();

        //guardar datos en bbdd (id generado de outfit, ids prendas utilizadas por orden, colores, usos)
        int id=BDO.introducirOutfit(name,arriba,abajo,entero,calzado,PrendasExtra,colores,usos);
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
            uploadTask.addOnCompleteListener(AniadirComplementos.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    uploading.setVisibility(View.GONE);
                    terminar.setEnabled(true);
                    if(id_viaje==-1) {
                        Intent intent = new Intent(AniadirComplementos.this, InfoOutfit.class);
                        intent.putExtra("username", username);
                        intent.putExtra("id", id);
                        intent.putExtra("Theme", usos.get(0));
                        intent.putExtra("Categoria", 1);
                        startActivity(intent);
                    }
                    else{
                        ArrayList<Integer> aux=new ArrayList<>();
                        aux.add(id);
                        BDO.editarOutfitsViajes(id_viaje,aux);
                        Intent intent = new Intent(AniadirComplementos.this, ViajeGuardado.class);
                        intent.putExtra("username", username);
                        intent.putExtra("id_viaje", id_viaje);
                        startActivity(intent);
                    }
                }
            });


        }
        else
            Toast.makeText(AniadirComplementos.this,"Tenemos problemas con la BBDD", Toast.LENGTH_SHORT).show();
    }
    private void editarImagen(){
        terminar.setEnabled(false);
        //guardar imagen de outfit
        add1.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);

        ArrayList<String> colores= todosColores();
        ArrayList<String> usos= todosUsos();

        //renovar datos en bbdd
        BDO.editarOutfit(id_editar,PrendasExtra,colores,usos);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        frameLayout.draw(canvas);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userRef = storageRef.child("outfits/" + username);

        //eliminamos la imagen anterior
        StorageReference idRef = userRef.child(String.valueOf(id_editar));
        idRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask= idRef.putBytes(data);
                uploading.setVisibility(View.VISIBLE);

                //añadimos la imagen nueva
                uploadTask.addOnCompleteListener(AniadirComplementos.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploading.setVisibility(View.GONE);
                        terminar.setEnabled(true);
                        Intent intent = new Intent(AniadirComplementos.this, InfoOutfit.class);
                        intent.putExtra("username",username);
                        intent.putExtra("id",id_editar);
                        intent.putExtra("Theme", theme);
                        intent.putExtra("Categoria", categoria);
                        startActivity(intent);
                    }
                });
            }
        });
    }
    private void desdeViaje(String name){
        //añadirlo a bbdd
        terminar.setEnabled(false);
        //guardar imagen de outfit
        add1.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);

        ArrayList<String> colores= todosColores();
        ArrayList<String> usos= todosUsos();
        //si no estaba la etiqueta por naturaleza se la ponemos adrede
        String t=BDP.getUsoExtra().get(posLista);
        if(!usos.contains(t)) usos.add(t);

        //guardar datos en bbdd (id generado de outfit, ids prendas utilizadas por orden, colores, usos)
        int id=BDO.introducirOutfit(name,arriba,abajo,entero,calzado,PrendasExtra,colores,usos);
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

            //volvemos al viaje
            uploadTask.addOnCompleteListener(AniadirComplementos.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    uploading.setVisibility(View.GONE);
                    terminar.setEnabled(true);
                    Intent intent=new Intent(AniadirComplementos.this, Seleccion_Outfits.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id_viaje",id_viaje);
                    intent.putExtra("veces", veces_todos);
                    intent.putExtra("vez_estancados", posLista);
                    intent.putIntegerArrayListExtra("outfits_anteriores",idsSelecionados);
                    startActivity(intent);
                }
            });


        }
        else
            Toast.makeText(AniadirComplementos.this,"Tenemos problemas con la BBDD", Toast.LENGTH_SHORT).show();
        //de vuelta a viaje

    }
}
