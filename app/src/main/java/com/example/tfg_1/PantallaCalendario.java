package com.example.tfg_1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_1.Armario.Outfits;
import com.example.tfg_1.Calendar.CalendarAdapter;
import com.example.tfg_1.CrearManual.InfoOutfit;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class PantallaCalendario  extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    String username;
    Button back,home;
    FloatingActionButton atras, adelante;
    TextView mesAnio;
    RecyclerView calendario;
    LocalDate diaSeleccionado;
    ArrayList<Integer> seleccionadosDias;
    ArrayList<Pair<Integer, Integer>> seleccionadosViajes;
    BaseDatosOutfits BDO;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        BDO=new BaseDatosOutfits(this);

        Intent intent= getIntent();
        if(intent!=null) {
            username=(intent.getStringExtra("username"));
            String aux= intent.getStringExtra("dia");
            if(aux!=null)
                diaSeleccionado=LocalDate.parse(aux);
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                diaSeleccionado=intent.getParcelableExtra("dia",LocalDate.class);
            }*/
        }
        calendario=findViewById(R.id.calendario);
        atras=findViewById(R.id.ATRAS);
        adelante=findViewById(R.id.ADELANTE);
        mesAnio=findViewById(R.id.mesAnio);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);

        if (diaSeleccionado==null)
            diaSeleccionado= LocalDate.now();
        setCalendarView();

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaSeleccionado=diaSeleccionado.minusMonths(1);
                setCalendarView();
            }
        });
        adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaSeleccionado=diaSeleccionado.plusMonths(1);
                setCalendarView();
            }
        });


        //Tabla de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }
    private void setCalendarView(){
        updateSeleccionados();
        mesAnio.setText(formatoMes(diaSeleccionado));
        ArrayList<String> diasMes=arrayDiasMes(diaSeleccionado);

        CalendarAdapter adapter= new CalendarAdapter(diasMes, seleccionadosDias,seleccionadosViajes, (CalendarAdapter.OnItemListener) this);
        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(getApplicationContext(),7);

        calendario.setLayoutManager(layoutManager);
        calendario.setAdapter(adapter);
    }

    /**
     * @param date
     * @return Un array list con los dias del mes de la fecha puesta
     */
    private ArrayList<String> arrayDiasMes(LocalDate date){
        ArrayList<String> diasMes=new ArrayList<>();
        YearMonth yearMonth=YearMonth.from(date);
        int cantDias=yearMonth.lengthOfMonth();

        LocalDate primerDia=diaSeleccionado.withDayOfMonth(1);
        int diaSemana=primerDia.getDayOfWeek().getValue();

        //en las 6 posibles semanas ponemos vacio antes de que empiece a contar el mes y despues
        // al resto ponemos el dia asociado
        for (int i=2;i<=7*6;i++){
            if(i<=diaSemana || i > cantDias+diaSemana)
                diasMes.add("");
            else
                diasMes.add(String.valueOf(i - diaSemana));
        }
        return diasMes;
    }
    private String formatoMes(LocalDate date){
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("MMMM yyyy",new Locale("es","ES"));
        return  date.format(formatter).toUpperCase();
    }
    //rellena seleccionados de los numeros que ya tienen outfits seleccionados
    private void updateSeleccionados(){
        int mes=diaSeleccionado.getMonthValue();
        String mes_s=String.valueOf(mes);
        if(mes<10)
            mes_s="0"+mes_s;
        String anio_mes=String.valueOf(diaSeleccionado.getYear())+"-"+mes_s+"-";

        seleccionadosDias=BDO.outfitsMes(anio_mes);
        //coger solo las del mes
        seleccionadosViajes=BDO.viajesMes(anio_mes);

    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals("")){
            if(Integer.valueOf(dayText)<10)
                dayText="0"+dayText;
            int mes=diaSeleccionado.getMonthValue();
            String mes_s=String.valueOf(mes);
            if(mes<10)
                mes_s="0"+mes_s;
            String fecha=String.valueOf(diaSeleccionado.getYear())+"-"+String.valueOf(mes_s)+"-"+dayText;
            switch (esOutfitViajeNada(dayText)){
                case 0:
                    Intent intent= new Intent(PantallaCalendario.this, Outfits.class);
                    intent.putExtra("username",username);
                    intent.putExtra("dia",fecha);
                    startActivity(intent);
                    break;
                case 1:
                    intent= new Intent(PantallaCalendario.this, InfoOutfit.class);
                    intent.putExtra("username",username);
                    int id=BDO.idOutfit(fecha);
                    intent.putExtra("id",id);
                    intent.putExtra("Theme",BDO.pedirUsoOutfit(id).get(0));
                    intent.putExtra("Categoria",1);
                    intent.putExtra("dia",fecha);
                    startActivity(intent);
                    break;
                case 2:
                    intent= new Intent(PantallaCalendario.this, ViajeGuardado.class);
                    intent.putExtra("username",username);
                    int id_viaje=BDO.idViaje(fecha);
                    intent.putExtra("id_viaje",id_viaje);
                    intent.putExtra("dia",fecha);
                    startActivity(intent);
            }
        }
    }

    @Override
    public void onItemLongClick(int adapterPosition, String text) {

        if(!text.equals("") && esOutfitViajeNada(text)==1){
            if(Integer.valueOf(text)<10)
                text="0"+text;

            int mes=diaSeleccionado.getMonthValue();
            String mes_s=String.valueOf(mes);
            if(mes<10)
                mes_s="0"+mes_s;
            String fecha=String.valueOf(diaSeleccionado.getYear())+"-"+String.valueOf(mes_s)+"-"+text;

            //eliminamos la fecha relacionada
            BDO.removeDayOutfit(fecha);
            Toast.makeText(this, "Borrado Outfit del "+ fecha, Toast.LENGTH_SHORT).show();
            setCalendarView();


        }
    }

    /**
     * @param dayText
     * @return 0 si vacio, 1 si outfitDia, 2 so viaje
     */
    private int esOutfitViajeNada(String dayText){
        int dia=Integer.valueOf(dayText);
        for(int selected: seleccionadosDias)
            if(dia==selected) return 1;

        for(Pair<Integer,Integer> pair: seleccionadosViajes){
            if (pair.first <= dia && dia <= pair.second)
                return 2;
        }
        return 0;
    }
}
