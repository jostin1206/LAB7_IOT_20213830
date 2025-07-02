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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.bumptech.glide.Glide;
import com.example.lab5.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

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

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private CloudStorage cloudStorage;

    private Button btnSeleccionarImagen, btnDescargarImagen;
    private TextView tvLinkImagen;
    private EditText etNombreDescarga;
    private ImageView ivImagenDescargada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        // Login anónimo antes de cualquier acción de Storage
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // ¡Ya tienes usuario anónimo!
                        } else {
                            Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


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

        // Inicializan CloudStorage
        //cloudStorage = new CloudStorage();
        cloudStorage = new CloudStorage(this);

// Vinculan los nuevos botones y vistas
        btnSeleccionarImagen = binding.btnSeleccionarImagen;
        btnDescargarImagen = binding.btnDescargarImagen;
        tvLinkImagen = binding.tvLinkImagen;
        etNombreDescarga = binding.etNombreDescarga;
        ivImagenDescargada = binding.ivImagenDescargada;
        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });

        btnDescargarImagen.setOnClickListener(v -> {
            String nombreArchivo = etNombreDescarga.getText().toString().trim();
            if (nombreArchivo.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre de la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            cloudStorage.obtenerArchivo(
                    nombreArchivo,
                    uri -> {
                        // Usa Glide para cargar la imagen en ivImagenDescargada
                        Glide.with(this).load(uri).into(ivImagenDescargada);
                        Toast.makeText(this, "Imagen descargada correctamente", Toast.LENGTH_SHORT).show();
                    },
                    e -> Toast.makeText(this, "Error al descargar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            String nombreArchivo = "foto_" + System.currentTimeMillis() + ".jpg";
            cloudStorage.guardarArchivo(
                    imageUri,
                    nombreArchivo,
                    uri -> {
                        //tvLinkImagen.setText("Link: " + uri.toString());
                        tvLinkImagen.setText("Imagen subida: " + nombreArchivo);
                        tvLinkImagen.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    },
                    e -> Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }
}