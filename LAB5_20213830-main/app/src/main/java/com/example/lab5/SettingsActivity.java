package com.example.lab5;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_NAME = "nombre_usuario";
    private static final String KEY_MSG = "mensaje_motivacional";
    private static final String KEY_HORAS = "frecuencia_motivacional";
    private static final String KEY_FREQ = "frecuencia_motivacional";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Cargamos datos existentes
        binding.editNombre.setText(preferences.getString(KEY_NAME, ""));
        binding.editMensaje.setText(preferences.getString(KEY_MSG, ""));
       // binding.editHoras.setText(String.valueOf(preferences.getInt(KEY_HORAS, 8)));

        int frecuencia = preferences.getInt(KEY_HORAS, -1);
        if (frecuencia != -1) {
            binding.editHoras.setText(String.valueOf(frecuencia));
        }


        binding.btnGuardar.setOnClickListener(v -> {
            String nombre = binding.editNombre.getText().toString();
            String mensaje = binding.editMensaje.getText().toString();
            String frecuenciaStr = binding.editHoras.getText().toString();

            if (nombre.isEmpty() || mensaje.isEmpty() || frecuenciaStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int horas;
            try {
                horas = Integer.parseInt(frecuenciaStr);
                if (horas <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Frecuencia inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_NAME, nombre);
            editor.putString(KEY_MSG, mensaje);
            editor.putInt(KEY_FREQ, horas);
            editor.apply();

            // Programar notificación motivacional
            long intervalo = horas * 60L * 60L * 1000L;
            long inicio = System.currentTimeMillis() + intervalo;

            Intent intent = new Intent(this, Recordatorio.class);
            intent.putExtra("nombre", mensaje);
            intent.putExtra("categoria", "Motivación");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    mensaje.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        inicio,
                        intervalo,
                        pendingIntent
                );
            }

            //Toast.makeText(this, "Configuración guardada y notificación activada", Toast.LENGTH_SHORT).show();
            finish(); // volver a la pantalla principal
        });




    }
}