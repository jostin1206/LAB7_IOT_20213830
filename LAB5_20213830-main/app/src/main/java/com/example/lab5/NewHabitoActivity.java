package com.example.lab5;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5.databinding.ActivityNewHabitoBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewHabitoActivity extends AppCompatActivity {

    private ActivityNewHabitoBinding binding;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_HABITOS = "lista_habitos";

    private int dia, mes, anio, hora, minuto;
    private String fechaHoraFinal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewHabitoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Fecha
        binding.btnFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                dia = dayOfMonth;
                mes = month + 1;
                anio = year;
                actualizarFechaHora();
            }, y, m, d);
            datePicker.show();
        });

        // Hora
        binding.btnHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                hora = hourOfDay;
                minuto = minute;
                actualizarFechaHora();
            }, h, m, true);
            timePicker.show();
        });

        // Guardamos el hábito
        binding.btnGuardarHabito.setOnClickListener(v -> {
            String nombre = binding.editNombreHabito.getText().toString();
            String categoria = binding.spinnerCategoria.getSelectedItem().toString();
            String frecuenciaStr = binding.editFrecuencia.getText().toString();

            if (nombre.isEmpty() || frecuenciaStr.isEmpty() || fechaHoraFinal.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int frecuencia = Integer.parseInt(frecuenciaStr);
            Habito nuevo = new Habito(nombre, categoria, frecuencia, fechaHoraFinal);

            List<Habito> lista = obtenerHabitos();
            lista.add(nuevo);
            guardarHabitos(lista);

            //Toast.makeText(this, "Hábito guardado", Toast.LENGTH_SHORT).show();



            // Programar recordatorio
           // long intervaloMillis = 10 * 1000L;
            long intervaloMillis = frecuencia * 60L * 60L * 1000L;

            // frecuencia en horas → milisegundos
            long tiempoInicial = System.currentTimeMillis() + intervaloMillis;

            Intent intent = new Intent(this, Recordatorio.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("categoria", categoria);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    nombre.hashCode(), // id único por hábito
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {

                //alarmManager.setExact(AlarmManager.RTC_WAKEUP, tiempoInicial, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoInicial, pendingIntent);
                //alarmManager.setExact(AlarmManager.RTC_WAKEUP, tiempoInicial, pendingIntent);


            }


            finish(); // devuele a la lista
        });
    }

    private void actualizarFechaHora() {
        fechaHoraFinal = String.format("%02d/%02d/%04d %02d:%02d", dia, mes, anio, hora, minuto);
        Toast.makeText(this, "Fecha y hora: " + fechaHoraFinal, Toast.LENGTH_SHORT).show();
    }

    private List<Habito> obtenerHabitos() {
        String json = preferences.getString(KEY_HABITOS, null);
        if (json != null) {
            Type tipoLista = new TypeToken<List<Habito>>() {}.getType();
            return new Gson().fromJson(json, tipoLista);
        } else {
            return new ArrayList<>();
        }
    }

    private void guardarHabitos(List<Habito> lista) {
        String json = new Gson().toJson(lista);
        preferences.edit().putString(KEY_HABITOS, json).apply();
    }
}