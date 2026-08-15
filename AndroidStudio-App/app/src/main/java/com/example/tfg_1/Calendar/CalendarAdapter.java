package com.example.tfg_1.Calendar;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_1.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> diasDeSemana;
    private final ArrayList<Integer> seleccionadosDias;
    private final ArrayList<Pair<Integer, Integer>> seleccionadosViajes;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> diasDeSemana, ArrayList<Integer> seleccionados, ArrayList<Pair<Integer, Integer>> seleccionadosViajes, OnItemListener onItemListener) {
        this.diasDeSemana = diasDeSemana;
        this.seleccionadosDias = seleccionados;
        this.seleccionadosViajes = seleccionadosViajes;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        //int layoutId = seleccionados.contains(viewType) ? R.layout.calendar_cell_outfit : R.layout.calendar_cell;

        View view =inflater.inflate(R.layout.calendar_cell,parent,false);
        ViewGroup.LayoutParams layoutParams= view.getLayoutParams();
        layoutParams.height= (int)(parent.getHeight()* 0.1666666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.diaDeSemana.setText(diasDeSemana.get(position));
        if(!diasDeSemana.get(position).equals("") && seleccionadosDias.contains(Integer.valueOf(diasDeSemana.get(position)))) {
            holder.diaDeSemana.setBackgroundColor(ContextCompat.getColor(holder.diaDeSemana.getContext(), R.color.verde_claro));
            holder.diaDeSemana.setTextColor(ContextCompat.getColor(holder.diaDeSemana.getContext(), R.color.white));
        }
        if(!diasDeSemana.get(position).equals("") && estaEnViaje(Integer.valueOf(diasDeSemana.get(position)))){
            ViewGroup.LayoutParams lp=holder.diaDeSemana.getLayoutParams();
            lp.width=150;
            holder.diaDeSemana.setLayoutParams(lp);
            holder.diaDeSemana.setBackgroundColor(ContextCompat.getColor(holder.diaDeSemana.getContext(), R.color.naranja));
            holder.diaDeSemana.setTextColor(ContextCompat.getColor(holder.diaDeSemana.getContext(), R.color.white));
        }
    }
    private boolean estaEnViaje(int dia){
        for(Pair<Integer,Integer> dupla : seleccionadosViajes) {
            if (dupla.first <= dia && dia <= dupla.second)
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return diasDeSemana.size();
    }
    public interface OnItemListener{
        void onItemClick(int position, String dayText);
        void onItemLongClick(int adapterPosition, String text);
    }
}
