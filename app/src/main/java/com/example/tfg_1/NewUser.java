package com.example.tfg_1;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Automatico.AutomaticoUso;
import com.example.tfg_1.Automatico.WeatherApi;
import com.example.tfg_1.Automatico.WeatherResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewUser extends AppCompatActivity {
    EditText username, password, realname, email, anio, codpost;
    Button crear;
    String us, pw, rn, em, year, ciudad;
    static String nombre1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        username=findViewById(R.id.user2);
        password=findViewById(R.id.pw2);
        realname=findViewById(R.id.realname);
        email=findViewById(R.id.email);
        anio=findViewById(R.id.edad);
        codpost=findViewById(R.id.codigoPostal);
        crear=findViewById(R.id.bttncrearUsuario);

        //para seguir con usuario y contraseña que se propuso en la pantalla anterior
        Intent intent = getIntent();
        if(intent!=null){
            String u=intent.getStringExtra("user");
            if(!TextUtils.isEmpty(u) && username!=null)
                username.setText(u);
            String p=intent.getStringExtra("pw");
            if(!TextUtils.isEmpty(p) && password!=null)
                password.setText(p);

        }
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 us = username.getText().toString();
                 pw = password.getText().toString();
                 rn = realname.getText().toString();
                 em = email.getText().toString();
                 year = anio.getText().toString();
                 ciudad = codpost.getText().toString();


                if (TextUtils.isEmpty(us) || TextUtils.isEmpty(pw) || TextUtils.isEmpty(rn) || TextUtils.isEmpty(year) || TextUtils.isEmpty(ciudad) || TextUtils.isEmpty(em))
                    Toast.makeText(NewUser.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();

                else
                    verificaNombreCiudad(ciudad);
            }

        });

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
                    guardarUsuario();
                } else {
                    //error
                    Toast.makeText(NewUser.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Maneja el error de la solicitud
                Toast.makeText(NewUser.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void guardarUsuario(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference();
        Usuario usuario=new Usuario(us,pw,em,rn,Integer.parseInt(year),ciudad);
        myRef.child("informacion_usuarios/"+us).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // La clave ya existe
                    Toast.makeText(NewUser.this,"Nombre de Usuario no disponible",Toast.LENGTH_SHORT).show();

                } else {
                    // La clave no existe, añade el nuevo valor
                    myRef.child("informacion_usuarios/"+us).setValue(usuario);
                    //añadimos los usos extras
                    ArrayList<String> elementos= new ArrayList<>();
                    elementos.add("Casual");
                    elementos.add("Trabajo");
                    elementos.add("Fiesta");
                    elementos.add("Deporte");
                    elementos.add("Cómodo");
                    elementos.add("Formal");
                    elementos.add("Piscina/Playa");
                    myRef.child("prendas/" + us + "/USOEXTRA").setValue(elementos);

                    Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                    intent.putExtra("username",us);
                    intent.putExtra("i",1);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
