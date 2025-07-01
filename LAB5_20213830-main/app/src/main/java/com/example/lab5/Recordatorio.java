package com.example.lab5;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class Recordatorio extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreHabito = intent.getStringExtra("nombre");
        String categoria = intent.getStringExtra("categoria");
        Log.d("Recordatorio", "Broadcast recibido para: " + nombreHabito);

        Toast.makeText(context, "Notificación para: " + nombreHabito, Toast.LENGTH_SHORT).show();
        String mensaje = "Es hora de: " + nombreHabito;

        // Creamos canal por categoría
        String canalId = "canal_" + categoria.toLowerCase();
        String canalNombre = "Canal de " + categoria;
        int importancia = NotificationManager.IMPORTANCE_DEFAULT;

        switch (categoria.toLowerCase()) {
            case "ejercicio":
                importancia = NotificationManager.IMPORTANCE_HIGH;
                break;
            case "alimentación":
                importancia = NotificationManager.IMPORTANCE_DEFAULT;
                break;
            case "sueño":
                importancia = NotificationManager.IMPORTANCE_LOW;
                break;
            case "lectura":
                importancia = NotificationManager.IMPORTANCE_MIN;
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, canalNombre, importancia);
            canal.setDescription("Recordatorios para la categoría " + categoria);
            canal.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }

        // Intent para abrir MainActivity al tocar la notificación
        Intent actividad = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                actividad,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construimos notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Recordatorio de hábito")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notiManager != null) {
            notiManager.notify(nombreHabito.hashCode(), builder.build());
        }
    }
}
