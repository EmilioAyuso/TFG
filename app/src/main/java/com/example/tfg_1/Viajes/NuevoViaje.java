package com.example.tfg_1.Viajes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Automatico.WeatherApi;
import com.example.tfg_1.Automatico.WeatherResponse;
import com.example.tfg_1.BaseDatosOutfits;
import com.example.tfg_1.NewUser;
import com.example.tfg_1.PantallaCalendario;
import com.example.tfg_1.PantallaInicio;
import com.example.tfg_1.R;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NuevoViaje extends AppCompatActivity {
    String username, nV,local;
    int ini,fin,mal,hoy;
    EditText nombreViaje, localizacion;
    TextView selecciona_i, selecciona_f, selecciona_m;
    BaseDatosOutfits BDO;
    Button back, home, calendar, acept;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_viaje);
        BDO=new BaseDatosOutfits(this);
        Intent intent=getIntent();

        if(intent!=null){
            username=intent.getStringExtra("username");
            nV=intent.getStringExtra("nombre");
            local=intent.getStringExtra("localizacion");
            ini=intent.getIntExtra("inicio",0);
            fin=intent.getIntExtra("fin",0);
            mal=intent.getIntExtra("maleta",0);
        }
        nombreViaje=findViewById(R.id.NombreViaje);
        localizacion=findViewById(R.id.Localizacion);
        selecciona_i=findViewById(R.id.seleccionarFecha1);
        selecciona_f=findViewById(R.id.seleccionarFecha2);
        selecciona_m=findViewById(R.id.seleccionarFecha3);
        acept=findViewById(R.id.button2);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        int dia,mes,anio;

        if(nV!=null)
            nombreViaje.setText(nV);
        if(local!=null)
            localizacion.setText(local);
        if(ini!=0){
            anio=ini/10000;
            mes=(ini-anio*10000)/100;
            dia=(ini-anio*10000-mes*100);
            selecciona_i.setText(String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio));
        }
        if(fin!=0) {
            anio=fin/10000;
            mes=(fin-anio*10000)/100;
            dia=(fin-anio*10000-mes*100);
            selecciona_f.setText(String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio));
        }
        if(mal!=0){
            anio=mal/10000;
            mes=(mal-anio*10000)/100;
            dia=(mal-anio*10000-mes*100);
            selecciona_m.setText(String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio));
        }
        Calendar c=Calendar.getInstance();
        hoy= c.get(Calendar.DAY_OF_MONTH)+ (c.get(Calendar.MONTH)+1)*100+(c.get(Calendar.YEAR))*10000;

        selecciona_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(seleccionarFecha(1));
            }
        });
        selecciona_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(seleccionarFecha(2));
            }
        });
        selecciona_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(seleccionarFecha(3));
            }
        });
        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombreViaje.getText().toString().isEmpty() || localizacion.getText().toString().isEmpty() || ini==0 || fin==0 || mal==0)
                    Toast.makeText(NuevoViaje.this,"Completa TODOS los campos",Toast.LENGTH_SHORT).show();
                else{
                    if(ini<mal)
                        Toast.makeText(NuevoViaje.this,"La fecha de organizacion de maleta debe ser anterior a la inicial",Toast.LENGTH_SHORT).show();
                    else if(ini>fin)
                        Toast.makeText(NuevoViaje.this,"La duracion del viaje debe ser positiva",Toast.LENGTH_SHORT).show();
                    else if(mal<hoy)
                        Toast.makeText(NuevoViaje.this,"Debes hacer la maleta como pronto Hoy",Toast.LENGTH_SHORT).show();
                    else if(ini==fin)
                        Toast.makeText(NuevoViaje.this,"Los viajes deben de ser de dos dias mínimo",Toast.LENGTH_SHORT).show();
                    else if(BDO.viajesSolopan(ElegirTipos.conversionFecha(ini),ElegirTipos.conversionFecha(fin)))
                        Toast.makeText(NuevoViaje.this,"Error: Este viaje se solapa con otro",Toast.LENGTH_SHORT).show();
                    else if(BDO.viajeSolapaDia(ElegirTipos.conversionFecha(ini),ElegirTipos.conversionFecha(fin)))
                        Toast.makeText(NuevoViaje.this,"Error: Este viaje se solapa con un outfit preparado",Toast.LENGTH_SHORT).show();
                    else{
                        //mirar bien tema localizacion
                        verificaNombreCiudad(localizacion.getText().toString());

                    }

                }
            }
        });

        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), MisViajes.class);
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
    private Intent seleccionarFecha(int pos){
        Intent intent= new Intent(NuevoViaje.this,Seleccionar_Fecha.class);
        intent.putExtra("username",username);
        if(!nombreViaje.getText().toString().isEmpty()){
            intent.putExtra("nombre",nombreViaje.getText().toString());
        }
        if(!localizacion.getText().toString().isEmpty()){
            intent.putExtra("localizacion",localizacion.getText().toString());
        }
        if(ini!=0){
            intent.putExtra("inicio",ini);
        }
        if(fin!=0){
            intent.putExtra("fin",fin);
        }
        if(mal!=0){
            intent.putExtra("maleta",mal);
        }
        intent.putExtra("pos",pos);
        return intent;
    }
    private void verificaNombreCiudad(String ciudad) {
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi= retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherApi.getWeatherData(ciudad, "d83085dd05fd2f9ce0539545c8937469", "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    // Existe la ciudad
                    //estan bien los datos
                    Intent intent=new Intent(NuevoViaje.this,ElegirTipos.class);
                    intent.putExtra("username",username);
                    intent.putExtra("nombre",nombreViaje.getText().toString());
                    intent.putExtra("localizacion",ciudad);
                    intent.putExtra("inicio",ini);
                    intent.putExtra("fin",fin);
                    intent.putExtra("maleta",mal);
                    startActivity(intent);
                } else {
                    //error
                    Toast.makeText(NuevoViaje.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Maneja el error de la solicitud
                Toast.makeText(NuevoViaje.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
