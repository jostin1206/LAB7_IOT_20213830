package com.example.lab5;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_NAME = "nombre_usuario";
    private static final String KEY_MSG = "mensaje_motivacional";
    private static final String IMAGE_FILENAME = "imagen_perfil.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

       // aqui se pide permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // Esto abre el diálogo para que el usuario apruebe
            }
        }*/


        // Mostramos saludo y mensaje guardado
       // String nombre = preferences.getString(KEY_NAME, "Usuario");

        String nombre = preferences.getString("nombre_usuario", "Usuario");
        //binding.textSaludo.setText("¡Hola, " + nombre + "!");


        String mensaje = preferences.getString(KEY_MSG, "¡Sigue con tus buenos hábitos!");

        binding.textSaludo.setText("¡Hola, " + nombre + "!");
        binding.textMensaje.setText(mensaje);
        /*
        binding.btnProbarNotificacion.setOnClickListener(v -> {
            Intent intent = new Intent(this, Recordatorio.class);
            intent.putExtra("nombre", "Caminar 10 minutos");
            intent.putExtra("categoria", "Ejercicio");
            sendBroadcast(intent);
        });*/


        // Cargamos la imagen si existe
        File imageFile = new File(getFilesDir(), IMAGE_FILENAME);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            binding.imagePerfil.setImageBitmap(bitmap);
        }

        // Cambiamos la imagen al hacer clic
        binding.imagePerfil.setOnClickListener(view -> abrirGaleria());

        // Navegamos a la pantalla de hábitos
        binding.btnVerHabitos.setOnClickListener(v -> {
            Intent intent = new Intent(this, HabitosActivity.class);
            startActivity(intent);
        });

        // Navegamos  a la pantalla de configuraciones
        binding.btnConfiguraciones.setOnClickListener(v -> {
            //Toast.makeText(this, "Configuraciones (pendiente)", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        });




    }


    @Override
    protected void onResume() {
        super.onResume();

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String nombre = preferences.getString(KEY_NAME, "Usuario");
        String mensaje = preferences.getString(KEY_MSG, "¡Sigue con tus buenos hábitos!");

        binding.textSaludo.setText("¡Hola, " + nombre + "!");
        binding.textMensaje.setText(mensaje);
    }


    // esto será un lanzador para seleccionar imagen de la galería
    ActivityResultLauncher<Intent> launcherImagen = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    try (InputStream inputStream = getContentResolver().openInputStream(uri);
                         FileOutputStream outputStream = openFileOutput(IMAGE_FILENAME, MODE_PRIVATE)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        binding.imagePerfil.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherImagen.launch(intent);
    }
}