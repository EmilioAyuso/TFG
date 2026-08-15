package com.example.tfg_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuario extends AppCompatActivity {
    TextView username, password, realname, email, year, ciudad;
    Button back,home,calendar, editar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);

        username=findViewById(R.id.NombreViaje);
        password=findViewById(R.id.FechaMaleta);
        realname=findViewById(R.id.nombreReal);
        email=findViewById(R.id.FechaFin);
        year=findViewById(R.id.Localizacion);
        ciudad=findViewById(R.id.FechaInicio);

        back=findViewById(R.id.BACK);
        home=findViewById(R.id.Home);
        calendar=findViewById(R.id.Calendar);
        editar=findViewById(R.id.button4);

        Intent intent= getIntent();
        if(intent!=null){
            username.setText(intent.getStringExtra("username"));
            password.setText(intent.getStringExtra("password"));
            realname.setText(intent.getStringExtra("nombreReal"));
            email.setText(intent.getStringExtra("email"));
            year.setText(String.valueOf(intent.getIntExtra("edad",0)));
            ciudad.setText(intent.getStringExtra("ciudad"));
        }
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Vamos a Cambiar Informacion del usuario (Menos el nombre de usuario)
                Intent intent = new Intent(getApplicationContext(),EditarPerfil.class);
                intent.putExtra("username",username.getText().toString());
                intent.putExtra("password",password.getText().toString());
                intent.putExtra("nombreReal",realname.getText().toString());
                intent.putExtra("email",email.getText().toString());
                intent.putExtra("edad",Integer.valueOf(year.getText().toString()));
                intent.putExtra("ciudad",ciudad.getText().toString());
                startActivity(intent);
            }
        });

        //Tabla Ayuda
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                intent.putExtra("username",username.getText().toString());
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                intent.putExtra("username",username.getText().toString());
                startActivity(intent);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PantallaCalendario.class);
                intent.putExtra("username",username.getText().toString());
                startActivity(intent);
            }
        });

    }
}
