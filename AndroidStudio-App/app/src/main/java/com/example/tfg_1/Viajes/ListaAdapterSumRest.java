package com.example.tfg_1.Viajes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tfg_1.R;

import java.util.ArrayList;

public class ListaAdapterSumRest extends ArrayAdapter<String>{
    ArrayList<String> itemList;
    private int[] counterArray;
    public ListaAdapterSumRest(@NonNull Context context, ArrayList<String> itemList) {
        super(context, 0,itemList);
        this.itemList=itemList;
        this.counterArray=new int[itemList.size()];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.lista_sum_rest,parent,false);

        TextView textViewItem = convertView.findViewById(R.id.textViewItem);
        TextView textViewCounter = convertView.findViewById(R.id.textViewCounter);
        Button buttonIncrement = convertView.findViewById(R.id.buttonIncrement);
        Button buttonDecrement = convertView.findViewById(R.id.buttonDecrement);

        //final int[] counter = {counterArray[position]}; // Contador asociado al elemento

        // Establecer el texto del elemento
        textViewItem.setText(itemList.get(position));

        // Manejar el botón de incremento
        buttonIncrement.setOnClickListener(v -> {
            counterArray[position]++;
            textViewCounter.setText(String.valueOf(counterArray[position]));

        });

        // Manejar el botón de decremento
        buttonDecrement.setOnClickListener(v -> {
            if (counterArray[position]> 0) {
                counterArray[position]--;
                textViewCounter.setText(String.valueOf(counterArray[position]));
            }
        });

        return convertView;
    }
    public int[] getContador(){
        return counterArray;
    }
}
