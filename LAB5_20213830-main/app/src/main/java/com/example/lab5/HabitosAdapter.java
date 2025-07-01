package com.example.lab5;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

public class HabitosAdapter extends RecyclerView.Adapter<HabitosAdapter.HabitoViewHolder> {



    public interface OnHabitoEliminadoListener {
        void onListaVacia();
    }



    private List<Habito> listaHabitos;
    private OnHabitoEliminadoListener listener;
  /*  public HabitosAdapter(List<Habito> listaHabitos) {
        this.listaHabitos = listaHabitos;
    }*/

    public HabitosAdapter(List<Habito> listaHabitos, OnHabitoEliminadoListener listener) {
        this.listaHabitos = listaHabitos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habito, parent, false);
        return new HabitoViewHolder(vista);
    }
/*
    @Override
    public void onBindViewHolder(@NonNull HabitoViewHolder holder, int position) {
        Habito habito = listaHabitos.get(position);
        holder.textNombre.setText(habito.getNombre());
        holder.textCategoria.setText("Categoría: " + habito.getCategoria());
        holder.textFrecuencia.setText("Cada " + habito.getFrecuenciaHoras() + " horas");
        holder.textFechaInicio.setText("Inicio: " + habito.getFechaHoraInicio());
    }*/


    @Override
    public void onBindViewHolder(@NonNull HabitoViewHolder holder, int position) {
        Habito habito = listaHabitos.get(position);
        holder.textNombre.setText(habito.getNombre());
        holder.textCategoria.setText("Categoría: " + habito.getCategoria());
        holder.textFrecuencia.setText("Cada " + habito.getFrecuenciaHoras() + " horas");
        holder.textFechaInicio.setText("Inicio: " + habito.getFechaHoraInicio());

        // Lógica del botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de eliminar este hábito?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        listaHabitos.remove(pos);
                        notifyItemRemoved(pos);

                        // Guardar cambios
                        SharedPreferences preferences = v.getContext().getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("lista_habitos", new Gson().toJson(listaHabitos));
                        editor.apply();

                        //Toast.makeText(v.getContext(), "Hábito eliminado", Toast.LENGTH_SHORT).show();

                        // Verificar si la lista está vacía
                        if (listaHabitos.isEmpty() && listener != null) {
                            listener.onListaVacia();
                        }
                    })

                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        if (listaHabitos.isEmpty() && listener != null) {
            listener.onListaVacia();
        }

    }





    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public static class HabitoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textCategoria, textFrecuencia, textFechaInicio;
        Button btnEliminar;

        public HabitoViewHolder(@NonNull View itemView) {

            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombreHabito);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            textFrecuencia = itemView.findViewById(R.id.textFrecuencia);
            textFechaInicio = itemView.findViewById(R.id.textFechaInicio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }



}
