package com.example.tfg_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Automatico.AutomaticoUso;
import com.example.tfg_1.Automatico.City;
import com.example.tfg_1.Automatico.ForecastApi;
import com.example.tfg_1.Automatico.ForecastResponse;
import com.example.tfg_1.Automatico.GeneradorOutfit;
import com.example.tfg_1.Automatico.InfoTemperature;
import com.example.tfg_1.Automatico.Weather;
import com.example.tfg_1.Automatico.Weather5Days;
import com.example.tfg_1.Automatico.WeatherApi;
import com.example.tfg_1.Automatico.WeatherResponse;
import com.example.tfg_1.CrearManual.Manual;
import com.example.tfg_1.Viajes.Seleccion_Outfits;
import com.example.tfg_1.Viajes.ViajeGuardado;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManualOAutomatic extends AppCompatActivity {
    String username, clima;
    int id_viaje, posLista;
    int[] veces_todos;
    ArrayList<Integer> idsSelecionados;
    Button manual, auto;
    LocalDate localDate;
    BaseDatosPrendas BD;
    BaseDatosOutfits BDO;
    ProgressBar pb;
    Button back,home,calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_automatics);
        BD=new BaseDatosPrendas(this);
        BDO=new BaseDatosOutfits(this);

        manual=findViewById(R.id.buttonManual);
        auto= findViewById(R.id.buttonAutomatic);
        pb=findViewById(R.id.progressBar11);
        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);

        Intent i=getIntent();
        if(i!=null) {
            username = i.getStringExtra("username");
            id_viaje=i.getIntExtra("id_viaje",-1);
            posLista=i.getIntExtra("vez_estancados",-1);
            if(posLista!=-1){
                veces_todos=i.getIntArrayExtra("veces");
                idsSelecionados=i.getIntegerArrayListExtra("outfits_anteriores");
            }
        }
        pb.setVisibility(View.INVISIBLE);


        //Selecciona Manual
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BD.pedirTodasFotosTipo("Calzados").isEmpty())
                    Toast.makeText(ManualOAutomatic.this,"Debes de tener algun tipo de Calzado guardado", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), Manual.class);
                    intent.putExtra("username", username);
                    intent.putExtra("id_viaje", id_viaje);
                    if (posLista != -1) {
                        intent.putExtra("veces", veces_todos);
                        intent.putExtra("vez_estancados", posLista);
                        intent.putIntegerArrayListExtra("outfits_anteriores", idsSelecionados);
                    }
                    startActivity(intent);
                }
            }
        });
        //Selecciona Automatico
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BD.pedirTodasFotosTipo("Calzados").isEmpty())
                    Toast.makeText(ManualOAutomatic.this,"Debes de tener algun tipo de Calzado guardado", Toast.LENGTH_SHORT).show();
                else {
                    //Calculamos el tiempo
                    pb.setVisibility(View.VISIBLE);
                    getLugar();
                }
            }
        });


        //Barra de Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_viaje==-1){
                    //pantalla inicio
                    Intent intent= new Intent(getApplicationContext(), PantallaInicio.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
                else if(posLista==-1){
                    //viajes guardados
                    Intent intent= new Intent(getApplicationContext(), ViajeGuardado.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id_viaje",id_viaje);
                    startActivity(intent);
                }
                else{
                    //seleccion outfit
                    Intent intent= new Intent(getApplicationContext(), Seleccion_Outfits.class);
                    intent.putExtra("username",username);
                    intent.putExtra("id_viaje",id_viaje);
                    intent.putExtra("veces",veces_todos);
                    intent.putExtra("vez_estancados",posLista);
                    intent.putExtra("outfits_anteriores",idsSelecionados);
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
    private void getLugar(){
        if(id_viaje==-1) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference myRef = database.getReference("informacion_usuarios/" + username);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //el usuario existe
                        String ciudad = snapshot.child("nombreCiudad").getValue(String.class);
                        getTiempo(ciudad);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            //siendo un viaje
            getTiempoViaje(BDO.getViaje(id_viaje));
        }
    }
    public void getTiempo(String lugar){
        Retrofit retrofit= new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi= retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherApi.getWeatherData(lugar, "d83085dd05fd2f9ce0539545c8937469", "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    // Procesa la respuesta aquí
                    WeatherResponse weatherResponse=response.body();

                    Weather w_actual=weatherResponse.getWeatherList().get(0);
                    InfoTemperature infoTemperature= weatherResponse.getInfoTemperature();

                    double latitud=weatherResponse.getCoordenadas().getLat();
                    boolean despejado=estaDespejado(w_actual.getDescription());
                    boolean lluviafloja=lluviaFloja(w_actual.getDescription());
                    double temp=infoTemperature.getFeels_like();

                    identificarTiempo(temp,latitud,despejado, lluviafloja);
                } else {
                    //error
                    Toast.makeText(ManualOAutomatic.this,  "Error en la solicitud: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Maneja el error de la solicitud
                Toast.makeText(ManualOAutomatic.this,  "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param viaje
     * Accedemos al tiempo que nos hará en el viaje
     * Intenta acceder al real (hasta 5 dias mas tarde) y sino vamos a estimar :)
     */
    public void getTiempoViaje(Viaje viaje){
        String lugar=viaje.getLocalizacion();
        int diasHastaInicio= LocalDate.parse(viaje.getFecha_inicio()).compareTo(LocalDate.now().plusDays(5));
        int mes_media=(Period.between(LocalDate.parse(viaje.getFecha_inicio()),LocalDate.parse(viaje.getFecha_fin()))).getMonths()+LocalDate.parse(viaje.getFecha_inicio()).getMonthValue();
        //si el viaje ya ha comenzado, o esta fuera de nuestro rango de 5 dias, estimamos clima
        if(diasHastaInicio > 0 || LocalDate.parse(viaje.getFecha_inicio()).isBefore(LocalDate.now()))
            estimarTiempo(lugar,mes_media);
        else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ForecastApi forecastApi = retrofit.create(ForecastApi.class);

            Call<ForecastResponse> call = forecastApi.getForecastData(lugar, "d83085dd05fd2f9ce0539545c8937469", "metric");

            call.enqueue(new Callback<ForecastResponse>() {
                @Override
                public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                    if (response.isSuccessful()) {
                        ForecastResponse response1 = response.body();
                        //calculo temperatura media
                        double temp = 0, it = 0;
                        localDate = LocalDate.parse(viaje.getFecha_inicio());
                        //lluvias si hay alguna pues se pone
                        boolean lluevemucho = false;
                        boolean lluviafloja = false;
                        double lat = response1.getCiudad().getCoordenadas().getLat();
                        for (Weather5Days wth : response1.getGeneralList()) {
                            //coger solo los del viaje
                            LocalDate aux=obtenerFecha(wth.getDt_txt());
                            if(aux.isAfter(LocalDate.parse(viaje.getFecha_fin())))
                                break;//cuando ya este fuera del viaje, paramos
                            else if(aux.isEqual(LocalDate.parse(viaje.getFecha_inicio())) || aux.isAfter(LocalDate.parse(viaje.getFecha_inicio()))) {
                                //solo contamos en fechas de viaje
                                it++;
                                temp = temp + wth.getMain().getFeels_like();
                                if (!estaDespejado(wth.getWeather().get(0).getDescription()) && !lluviaFloja(wth.getWeather().get(0).getDescription()))
                                    lluevemucho = true;
                                if (lluviaFloja(wth.getWeather().get(0).getDescription()))
                                    lluviafloja = true;
                            }
                        }
                        temp = temp / it;
                        if (lluevemucho)
                            identificarTiempo(temp, lat, false, false);
                        else if (lluviafloja)
                            identificarTiempo(temp, lat, false, true);
                        else
                            identificarTiempo(temp, lat, true, false);

                    } else {
                        //error
                        Toast.makeText(ManualOAutomatic.this, "Error en la solicitud: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ForecastResponse> call, Throwable t) {
                    // Maneja el error de la solicitud
                    Toast.makeText(ManualOAutomatic.this, "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Identifica de que tipo de tiempo se trata:
     * VERANO:Mucho calor + sin lluvia o mucho calor + con lluvia
     * INVIERNO: Mucho frio + sin/con lluvia
     * PRIMAVERA/OTOÑO: temperaturas normales + sin/con lluvia
     */
    public void identificarTiempo(double temp, double latitud,boolean despejado, boolean lluviafloja){

        if(localDate==null){
            localDate=LocalDate.now();
        }
        if(temp>20){
            //VERANO
            if(despejado)
                clima="Verano sin";
            else if(lluviafloja)
                clima="Verano floja";
            else
                clima="Verano con";
        }
        else if(temp<10){
            //INVIERNO
            if(despejado)
                clima="Invierno sin";
            else
                clima="Invierno con";
        }
        else{
            //PRIMAVERA U OTOÑO
            int mes=localDate.getMonthValue();
            //lo identificamos por la fecha y latitud
            if((latitud>=0 && mes<9 && mes>3) || (latitud<0 && (mes>=9 || mes<=3))){
                //PRIMAVERA
                if(despejado)
                    clima="Primavera sin";
                else
                    clima="Primavera con";
            }
            else{
                //OTOÑO
                if(despejado)
                    clima="Otoño sin";
                else
                    clima="Otoño con";
            }
        }
        hacerIntent();
    }
    private boolean estaDespejado(String description){
        return (Objects.equals(description, "clear sky") || Objects.equals(description, "few clouds") || Objects.equals(description, "scattered clouds")) || (Objects.equals(description, "broken clouds")|| (Objects.equals(description, "mist")));
    }
    private boolean lluviaFloja(String description){
        return Objects.equals(description, "shower rain");
    }

    /**
     * @param lugar
     * @param mes
     * Nos vamos a inventar el tiempo segun el mes del viaje su estacion
     */
    private void estimarTiempo(String lugar, int mes){
        if(LocalDate.now().getMonthValue()==mes)//si el viaje es del mismo mes vamos a suponer que el tiempo es como hoy
            getTiempo(lugar);
        else {
            //calculamos latitud
            Retrofit retrofit= new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            WeatherApi weatherApi= retrofit.create(WeatherApi.class);

            Call<WeatherResponse> call = weatherApi.getWeatherData(lugar, "d83085dd05fd2f9ce0539545c8937469", "metric");

            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful()) {
                        // Procesa la respuesta aquí
                        WeatherResponse weatherResponse=response.body();

                        Weather w_actual=weatherResponse.getWeatherList().get(0);
                        InfoTemperature infoTemperature= weatherResponse.getInfoTemperature();

                        double latitud=weatherResponse.getCoordenadas().getLat();
                        if(latitud>0){
                            //hemisferio norte
                            //12-3 invierno, 4-6 prim, 7-9 verano, 10-12 otonio
                            if(mes==12 || mes<4){
                                //invierno
                                clima="Invierno con";
                            } else if (mes>=4 && mes<=6) {
                                //prim
                                clima="Primavera con";
                            } else if (mes>=7 && mes<=9) {
                                //verano
                                clima="Verano floja";
                            }
                            else{
                                //otoño
                                clima="Otoño con";
                            }
                        }
                        else{
                            //hemisferio sur
                            if(mes==12 || mes<4){
                                //verano
                                clima="Verano floja";
                            } else if (mes>=4 && mes<=6) {
                                //otoño
                                clima="Otoño con";
                            } else if (mes>=7 && mes<=9) {
                                //invierno
                                clima="Invierno con";
                            }
                            else{
                                //prim
                                clima="Primavera con";
                            }
                        }
                        hacerIntent();

                    } else {
                        //error
                        Toast.makeText(ManualOAutomatic.this,  "Error en la solicitud: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    // Maneja el error de la solicitud
                    Toast.makeText(ManualOAutomatic.this,  "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }
    private void hacerIntent(){
        //hacemos intent
        pb.setVisibility(View.INVISIBLE);
        if(posLista==-1) {
            Intent intent = new Intent(getApplicationContext(), AutomaticoUso.class);
            intent.putExtra("username", username);
            intent.putExtra("clima", clima);
            intent.putExtra("id_viaje", id_viaje);
            startActivity(intent);
        }
        else{
            //intent deberia ser directamente a GeneraforOutfit
            String uso=BD.getUsoExtra().get(posLista);
            if(identificarProblemasAUX(uso)){
                Intent intent = new Intent(getApplicationContext(), GeneradorOutfit.class);
                intent.putExtra("username", username);
                intent.putExtra("uso",uso);
                intent.putExtra("clima", clima);
                intent.putExtra("id_viaje", id_viaje);
                intent.putExtra("veces", veces_todos);
                intent.putExtra("vez_estancados", posLista);
                intent.putIntegerArrayListExtra("outfits_anteriores", idsSelecionados);
                startActivity(intent);
            }

        }
    }

    /**
     * @param item
     * igual que el de automaticUso
     */
    private boolean identificarProblemasAUX(String item){

        boolean partesArribaCortas=(!BD.pedirTodasFotosTipoUso("Camisetas",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Camisas/Blusas",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Polos",item).isEmpty());
        boolean partesAbajoCortas=(!BD.pedirTodasFotosTipoUso("Pantalones Cortos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Bañadores",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Faldas",item).isEmpty());
        boolean partesAbajoLargas=(!BD.pedirTodasFotosTipoUso("Pantalones Largos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Faldas",item).isEmpty());
        boolean partesCompletas=(!BD.pedirTodasFotosTipoUso("Vestidos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Monos",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Trajes",item).isEmpty());
        boolean partesAbrigoMedio=(!BD.pedirTodasFotosTipoUso("Jerseis",item).isEmpty() || !BD.pedirTodasFotosTipoUso("Sudaderas",item).isEmpty());
        boolean abrigo=(!BD.pedirTodasFotosTipoUso("Abrigos",item).isEmpty());

        if(BD.pedirTodasFotosTipoUso("Calzados",item).isEmpty())
            Toast.makeText(this, "Necesitas calzado disponible del tipo "+item, Toast.LENGTH_SHORT).show();
        else if(clima.startsWith("Verano")){
            if(clima.equals("Verano con") && !abrigo)//si no tenemos abrigo y llueve mucho
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(clima.equals("Verano floja") && !partesAbrigoMedio)//si no tenemos jersey/sudadera y llueve flojo
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+ item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoCortas) )//sino podemos hacer un outfit completo
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo corta, de tipo "+item, Toast.LENGTH_LONG).show();
            else
                return true;
        }
        else if(clima.startsWith("Primavera")){
            if(clima.equals("Primavera con") && !abrigo)//si no tenemos abrigo y llueve
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesAbrigoMedio)
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoLargas))
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo larga de tipo "+item, Toast.LENGTH_LONG).show();
            else
                return true;
        }
        else{
            //otoño-invierno
            if(!abrigo)//si no tenemos abrigo
                Toast.makeText(this, "Debes de tener algún abrigo disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesAbrigoMedio)
                Toast.makeText(this, "Debes de tener algún jersey o sudadera disponible de tipo "+item, Toast.LENGTH_SHORT).show();
            else if(!partesCompletas && (!partesArribaCortas || !partesAbajoLargas))
                Toast.makeText(this, "Debes de tener alguna prenda de cuerpo completo o alguna parte de arriba y de abajo larga de tipo "+item, Toast.LENGTH_LONG).show();
            else
                return true;
        }
        return false;
    }
    public static LocalDate obtenerFecha(String fechaHora) {
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date fecha = formatoEntrada.parse(fechaHora);
            String soloFecha = formatoSalida.format(fecha);
            return LocalDate.parse(soloFecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
