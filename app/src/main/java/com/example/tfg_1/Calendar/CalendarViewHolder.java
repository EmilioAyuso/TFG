package com.example.tfg_1.Calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_1.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public final TextView diaDeSemana;
    private final CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        diaDeSemana= itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void onClick(View view){
        onItemListener.onItemClick(getAdapterPosition(),(String) diaDeSemana.getText());
    }
    @Override
    public boolean onLongClick(View view) {
        onItemListener.onItemLongClick(getAdapterPosition(), (String) diaDeSemana.getText());
        return true;
    }
}
