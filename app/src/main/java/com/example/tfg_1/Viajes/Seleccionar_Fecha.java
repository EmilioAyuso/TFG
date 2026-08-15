package com.example.tfg_1.Viajes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_1.R;

public class Seleccionar_Fecha extends AppCompatActivity {
    TextView date;
    int pos, ini,fin,mal;
    String fecha_x;
    DatePicker calend;
    Button button;
    String username, nV,local;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_fecha);
        Intent intent=getIntent();
        if(intent!=null){
            username=intent.getStringExtra("username");
            pos=intent.getIntExtra("pos",0);

            nV=intent.getStringExtra("nombre");
            local=intent.getStringExtra("localizacion");
            ini=intent.getIntExtra("inicio",0);
            fin=intent.getIntExtra("fin",0);
            mal=intent.getIntExtra("maleta",0);

        }
        date=findViewById(R.id.editTextDate);
        calend=findViewById(R.id.textView16);
        button=findViewById(R.id.button);
        TextView titulo=findViewById(R.id.Titulo);

        String hoy=String.valueOf(calend.getDayOfMonth())+"/"+String.valueOf(calend.getMonth()+1)+"/"+String.valueOf(calend.getYear());
        int anio=calend.getYear(),mes=calend.getMonth()+1,dia=calend.getDayOfMonth();

        switch (pos){
            case 1:fecha_x="inicio";
                titulo.setText("INICIO");
                if(ini!=0){
                    anio=ini/10000;
                    mes=(ini-anio*10000)/100;
                    dia=(ini-anio*10000-mes*100);
                    hoy=String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio);
                }
                break;
            case 2:fecha_x="fin";
                titulo.setText("FIN");
                if(fin!=0){
                    anio=fin/10000;
                    mes=(fin-anio*10000)/100;
                    dia=(fin-anio*10000-mes*100);
                    hoy=String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio);
                }
                break;
            default:fecha_x="maleta";
                titulo.setText("MALETA");
                if(mal!=0){
                    anio=mal/10000;
                    mes=(mal-anio*10000)/100;
                    dia=(mal-anio*10000-mes*100);
                    hoy=String.valueOf(dia)+"/"+String.valueOf(mes)+"/"+String.valueOf(anio);
                }
        }
        date.setText(hoy);

        calend.init(anio, mes-1, dia, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                date.setText(String.valueOf(i2)+"/"+String.valueOf(i1+1)+"/"+String.valueOf(i));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date.getText().toString().isEmpty()){
                    Toast.makeText(Seleccionar_Fecha.this,"Selecciona un dia del calendario",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(Seleccionar_Fecha.this,NuevoViaje.class);
                    intent.putExtra("username",username);
                    if(nV!=null)
                        intent.putExtra("nombre",nV);
                    if(local!=null)
                        intent.putExtra("localizacion",local);
                    if(pos==1){
                        if(fin!=0)
                            intent.putExtra("fin",fin);
                        if(mal!=0)
                            intent.putExtra("maleta",mal);
                    }
                    else if(pos==2){
                        if(ini!=0)
                            intent.putExtra("inicio",ini);
                        if(mal!=0)
                            intent.putExtra("maleta",mal);
                    }
                    else if (pos==3){
                        if(ini!=0)
                            intent.putExtra("inicio",ini);
                        if(fin!=0)
                            intent.putExtra("fin",fin);
                    }
                    intent.putExtra(fecha_x,calend.getDayOfMonth()+(calend.getMonth()+1)*100+calend.getYear()*10000);
                    startActivity(intent);
                }
            }
        });
    }
}
