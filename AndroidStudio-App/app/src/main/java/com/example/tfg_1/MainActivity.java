package com.example.tfg_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button log, reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.user);
        password= findViewById(R.id.password);
        log= findViewById(R.id.login);
        reg = findViewById(R.id.register);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=username.getText().toString();
                String pw= password.getText().toString();

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pw))
                    Toast.makeText(MainActivity.this,"Completa todos los campos",Toast.LENGTH_SHORT).show();
                else{
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://dress-app-5605c-default-rtdb.europe-west1.firebasedatabase.app");
                    DatabaseReference myRef = database.getReference("informacion_usuarios/"+user);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                //el usuario existe
                                String pw_verdadera=snapshot.child("password").getValue(String.class);
                                if(pw_verdadera.equals(pw)){
                                    Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                                    intent.putExtra("username",user);
                                    intent.putExtra("i",1);
                                    startActivity(intent);
                                }
                                else
                                    Toast.makeText(MainActivity.this,"Contraseña incorrecta",Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(MainActivity.this,"Usuario incorrecto",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    /*
                    Boolean check = DB.checkUsernamePw(user,pw);
                    if(check){
                        Intent intent = new Intent(getApplicationContext(),PantallaInicio.class);
                        intent.putExtra("username",user);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(MainActivity.this,"Usuario o Contraseña incorrectos",Toast.LENGTH_SHORT).show();
*/
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=username.getText().toString();
                String pw= password.getText().toString();
                Intent intent = new Intent(getApplicationContext(),NewUser.class);

                intent.putExtra("user",user);
                intent.putExtra("pw",pw);
                startActivity(intent);
            }
        });
    }
}