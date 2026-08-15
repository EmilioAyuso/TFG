package com.example.tfg_1.Armario;

import static com.example.tfg_1.R.color.amarillo;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.tfg_1.R;

import java.util.Arrays;
import java.util.List;

public class ListaAdapter extends ArrayAdapter<String> {
    private List<Integer> colors;
    private List<Integer> destacados;
    Boolean col;
    public ListaAdapter(@NonNull Context context, List<String> elementos,List<Integer> destacados, boolean col) {
        super(context, R.layout.lista_colores, elementos);
        colors=rellenarColores();
        this.destacados=destacados;
        this.col=col;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.lista_colores, null);
        }

        TextView textView = view.findViewById(R.id.textViewColor);
        textView.setText(getItem(position));
        if(col) {
            // Asignar un color diferente a cada elemento según la posición
            int color = colors.get(position);
            textView.setBackgroundColor(color);
        }
        if(getItem(position).equals("AÑADIR OTRO USO")){
            textView.setBackgroundColor(R.drawable.seleccionador_lista);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(this.getContext(),R.color.white));
        }

        //destaca seleccionado
        if(destacados!=null && destacados.contains(position)){
            textView.setBackgroundColor(R.drawable.seleccionador_lista);
        }

        return view;
    }
    private List<Integer> rellenarColores(){
        List<Integer> lista;
        lista= Arrays.asList(ContextCompat.getColor(this.getContext(),R.color.rojo),ContextCompat.getColor(this.getContext(),R.color.naranja),ContextCompat.getColor(this.getContext(),R.color.amarillo),
                ContextCompat.getColor(this.getContext(),R.color.verde_claro),ContextCompat.getColor(this.getContext(),R.color.verde_oscuro),ContextCompat.getColor(this.getContext(),R.color.azul_claro),ContextCompat.getColor(this.getContext(),R.color.azul),ContextCompat.getColor(this.getContext(),R.color.azul_marino),
                ContextCompat.getColor(this.getContext(),R.color.morado),ContextCompat.getColor(this.getContext(),R.color.rosa),ContextCompat.getColor(this.getContext(),R.color.beige),
                ContextCompat.getColor(this.getContext(),R.color.marron), ContextCompat.getColor(this.getContext(),R.color.gris),ContextCompat.getColor(this.getContext(),R.color.white),ContextCompat.getColor(this.getContext(),R.color.black));
        return lista;

    }

}
