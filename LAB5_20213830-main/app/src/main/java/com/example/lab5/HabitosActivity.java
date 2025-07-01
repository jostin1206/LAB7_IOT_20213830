package com.example.lab5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab5.databinding.ActivityHabitosBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HabitosActivity extends AppCompatActivity {

    private ActivityHabitosBinding binding;
    private List<Habito> listaHabitos;
    private HabitosAdapter adapter;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_HABITOS = "lista_habitos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Leer hábitos guardados
        String jsonHabitos = preferences.getString(KEY_HABITOS, null);
        if (jsonHabitos != null) {
            Type tipoLista = new TypeToken<List<Habito>>() {}.getType();
            listaHabitos = new Gson().fromJson(jsonHabitos, tipoLista);
        } else {
            listaHabitos = new ArrayList<>();
        }

        // Configuramos RecyclerView
        //adapter = new HabitosAdapter(listaHabitos);
        adapter = new HabitosAdapter(listaHabitos, () -> binding.textVacio.setVisibility(View.VISIBLE));

        binding.recyclerHabitos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHabitos.setAdapter(adapter);

        // Botón para registrar nuevo hábito
        binding.btnNuevoHabito.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewHabitoActivity.class);
            startActivity(intent);
        });

        listaHabitos = new ArrayList<>();
        adapter = new HabitosAdapter(listaHabitos, () -> binding.textVacio.setVisibility(View.VISIBLE));
        binding.recyclerHabitos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHabitos.setAdapter(adapter);

    }
    /*
    @Override
    protected void onResume() {
        super.onResume();
        cargarHabitos();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        listaHabitos.clear();
        listaHabitos.addAll(obtenerHabitos());
        adapter.notifyDataSetChanged();

        if (listaHabitos.isEmpty()) {
            binding.textVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textVacio.setVisibility(View.GONE);
        }
    }
    private List<Habito> obtenerHabitos() {
        String jsonHabitos = preferences.getString(KEY_HABITOS, null);
        if (jsonHabitos != null) {
            Type tipoLista = new TypeToken<List<Habito>>() {}.getType();
            return new Gson().fromJson(jsonHabitos, tipoLista);
        } else {
            return new ArrayList<>();
        }
    }

    /*
    private void cargarHabitos() {
        String jsonHabitos = preferences.getString(KEY_HABITOS, null);
        if (jsonHabitos != null) {
            Type tipoLista = new TypeToken<List<Habito>>() {}.getType();
            listaHabitos = new Gson().fromJson(jsonHabitos, tipoLista);
        } else {
            listaHabitos = new ArrayList<>();
        }

        //adapter = new HabitosAdapter(listaHabitos);
        adapter = new HabitosAdapter(listaHabitos, () -> binding.textVacio.setVisibility(View.VISIBLE));


        binding.recyclerHabitos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHabitos.setAdapter(adapter);
        //mostramos mensaje si no hay nada
        if (listaHabitos.isEmpty()) {
            binding.textVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textVacio.setVisibility(View.GONE);
        }

    }*/

    private void cargarHabitos() {
        String jsonHabitos = preferences.getString(KEY_HABITOS, null);
        if (jsonHabitos != null) {
            Type tipoLista = new TypeToken<List<Habito>>() {}.getType();
            listaHabitos = new Gson().fromJson(jsonHabitos, tipoLista);
        } else {
            listaHabitos = new ArrayList<>();
        }

        // IMPORTANTE: recrea el adapter con la nueva lista actualizada
        adapter = new HabitosAdapter(listaHabitos, () -> binding.textVacio.setVisibility(View.VISIBLE));
        binding.recyclerHabitos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHabitos.setAdapter(adapter);

        // Mostrar u ocultar mensaje vacío
        if (listaHabitos.isEmpty()) {
            binding.textVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textVacio.setVisibility(View.GONE);
        }
    }




}