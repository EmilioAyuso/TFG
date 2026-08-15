package com.example.tfg_1.Viajes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Armario.InfoPrenda;
import com.example.tfg_1.Armario.Outfits;
import com.example.tfg_1.Armario.PrendaAdapter;
import com.example.tfg_1.Armario.PrendaClasificada;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.CrearManual.InfoOutfit;
import com.example.tfg_1.CrearManual.OutfitClasificado;
import com.example.tfg_1.ManualOAutomatic;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;
import com.example.tfg_1.Viaje;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViajeGuardado extends AppCompatActivity {
    String username;
    int id,left;
    ProgressBar pb;
    TextView titulo, fechas;
    GridView fotos;
    Viaje viaje;
    BaseDatosOutfits BD;
    ArrayList<Integer> idFotos, listaOrdenid;
    String diaSeleccionado;
    Button back, home, calendar, aniadir;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viaje_guardado);
        BD=new BaseDatosOutfits(this);
        Intent i=getIntent();
        if(i!=null){
            username=i.getStringExtra("username");
            id=i.getIntExtra("id_viaje",0);
            diaSeleccionado=i.getStringExtra("dia");
        }
        pb=findViewById(R.id.progressBar10);
        titulo=findViewById(R.id.txttheme3);
        fechas=findViewById(R.id.textView17);
        fotos=findViewById(R.id.listaRopa);
        aniadir=findViewById(R.id.buttonCrear);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        viaje=BD.getViaje(id);
        titulo.setText(viaje.getNombre_viaje());
        try {
            fechas.setText(conversorFecha(viaje.getFecha_inicio())+" - "+conversorFecha(viaje.getFecha_fin()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        actualizarFotos();
        fotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id_f= listaOrdenid.get(i);
                String uso=BD.pedirUsoOutfit(id_f).get(0);
                Intent intent=new Intent(getApplicationContext(), InfoOutfit.class);
                intent.putExtra("id",id_f);
                intent.putExtra("username",username);
                intent.putExtra("Theme", uso);
                intent.putExtra("Categoria", 1);
                intent.putExtra("viaje",id);
                startActivity(intent);
            }
        });
        fotos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id_f = listaOrdenid.get(i);
                if(idFotos.size()>1)
                    ventanaEmergenteBorrarOutfit(id_f,false);
                else
                    ventanaEmergenteBorrarOutfit(id_f,true);
                return true;
            }
        });
        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventanaEmergenteAñadir();
            }
        });



        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diaSeleccionado!=null){
                    Intent intent = new Intent(getApplicationContext(), PantallaCalendario.class);
                    intent.putExtra("username", username);
                    intent.putExtra("dia",diaSeleccionado);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MisViajes.class);
                    intent.putExtra("username", username);
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

    /**
     * @param "2023-12-11"
     * @return "11/12/2023"
     */
    private String conversorFecha(String fecha) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Crear un formato de fecha para la salida
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = inputFormat.parse(fecha);
        // Formatear la fecha en el nuevo formato
        return outputFormat.format(date);
    }
    private void actualizarFotos(){
        viaje=BD.getViaje(id);
        //pedimos los outfits con esa etiqueta
        List<byte[]> listafotos=new ArrayList<>();
        listaOrdenid=new ArrayList<>();

        idFotos=viaje.getIds_outfit();

        if(idFotos==null)
            pb.setVisibility(View.INVISIBLE);
        else{
            //los ponemos en fotos desde la bbdd externa
            FirebaseStorage storage = FirebaseStorage.getInstance();
            left=idFotos.size();
            pb.setVisibility(View.VISIBLE);
            for (int id : idFotos) {
                StorageReference storageRef = storage.getReference();
                StorageReference islandRef=storageRef.child("outfits/"+username+"/"+id);
                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        listafotos.add(bytes);
                        listaOrdenid.add(id);
                        PrendaAdapter adapter= new PrendaAdapter(ViajeGuardado.this,listafotos);
                        fotos.setAdapter(adapter);
                        left--;
                        if(left==0)
                            pb.setVisibility(View.INVISIBLE);
                    }
                });
            }
            PrendaAdapter adapter= new PrendaAdapter(this,listafotos);
            fotos.setAdapter(adapter);
        }
    }
    public void ventanaEmergenteBorrarOutfit(int id_f, boolean ultimo) {
        AlertDialog.Builder elegir= new AlertDialog.Builder(ViajeGuardado.this);
        elegir.setCancelable(false).setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("DESVINCULAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(ultimo){
                    BD.eliminarViaje(id);
                    Intent intent= new Intent(getApplicationContext(), MisViajes.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
                else {
                    BD.eliminarOutfitViaje(id, id_f);
                    actualizarFotos();
                }
            }
        });
        AlertDialog titulo= elegir.create();
        if(ultimo)
            titulo.setTitle("Si desvinculas este último Outfit, borraras los datos de tu Viaje");
        else
            titulo.setTitle("¿Seguro que quieres desvincular el Outfit a este viaje?:");
        titulo.show();
    }
    public void ventanaEmergenteAñadir() {
        AlertDialog.Builder elegir= new AlertDialog.Builder(ViajeGuardado.this);
        elegir.setCancelable(true).setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(ViajeGuardado.this, ManualOAutomatic.class);
                intent.putExtra("username",username);
                intent.putExtra("id_viaje",id);
                startActivity(intent);
            }
        }).setNegativeButton("Elegir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(ViajeGuardado.this, Outfits.class);
                intent.putExtra("username",username);
                intent.putExtra("id_viaje",id);
                startActivity(intent);
            }
        });
        AlertDialog titulo= elegir.create();
        titulo.setTitle("Elegir Outfit ya creado o Crear uno nuevo:");
        titulo.show();
    }
}
