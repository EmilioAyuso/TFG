package com.example.tfg_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.Automatico.WeatherApi;
import com.example.tfg_1.Automatico.WeatherResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarPerfil extends AppCompatActivity {
    TextView realname;
    String username, pw, rn, mail, city;
    int age;
    EditText password, email, edad, ciudad;
    Button back,home,calendar, guardar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        Intent intent= getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            pw=intent.getStringExtra("password");
            rn=intent.getStringExtra("nombreReal");
            mail=intent.getStringExtra("email");
            city=intent.getStringExtra("ciudad");
            age=intent.getIntExtra("edad",0);

        }

        password=findViewById(R.id.FechaMaleta);
        realname=findViewById(R.id.nombreReal);
        email=findViewById(R.id.FechaFin);
        edad=findViewById(R.id.Localizacion);
        ciudad=findViewById(R.id.FechaInicio);

        realname.setText(rn);
        edad.setText(String.valueOf(age));
        email.setText(mail);
        ciudad.setText(city);
        password.setText(pw);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);
        guardar=findViewById(R.id.button4);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edad.getText().toString().isEmpty() || ciudad.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                    Toast.makeText(EditarPerfil.this, "No dejes ningún campo vacío", Toast.LENGTH_SHORT).show();
                else
                    verificaNombreCiudad(ciudad.getText().toString());
            }
        });

        //Tabla Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PerfilUsuario.class);
                intent.putExtra("username",username);
                intent.putExtra("password",pw);
                intent.putExtra("nombreReal",rn);
                intent.putExtra("email",mail);
                intent.putExtra("edad",age);
                intent.putExtra("ciudad",city);
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
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaCalendario.class);
                intent.putExtra("username",username);
                startActivity(intent);
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
                    guardarDatos(ciudad);
                } else {
                    //error
                    Toast.makeText(EditarPerfil.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Maneja el error de la solicitud
                Toast.makeText(EditarPerfil.this, "No existe esa ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void guardarDatos(String ciudad){
        //guardamos los datos del usuario y volvemos a perfil de usuario
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference();
        Usuario usuario=new Usuario(username,password.getText().toString(),email.getText().toString(),rn,Integer.parseInt(edad.getText().toString()),ciudad);

        myRef.child("informacion_usuarios/"+username).removeValue();
        myRef.child("informacion_usuarios/"+username).setValue(usuario);

        Intent intent = new Intent(getApplicationContext(),PerfilUsuario.class);
        intent.putExtra("username",username);
        intent.putExtra("password",password.getText().toString());
        intent.putExtra("nombreReal",realname.getText().toString());
        intent.putExtra("email",email.getText().toString());
        intent.putExtra("edad",Integer.valueOf(edad.getText().toString()));
        intent.putExtra("ciudad",ciudad);
        startActivity(intent);

    }
}
