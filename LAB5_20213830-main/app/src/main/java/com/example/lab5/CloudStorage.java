package com.example.lab5;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.InputStream;

public class CloudStorage {

    private final FirebaseStorage storage;
    private final Context context;

    public CloudStorage(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    // metodo para subir archvios
    public void guardarArchivo(Uri fileUri, String nombreArchivo, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
        try {
            InputStream stream = context.getContentResolver().openInputStream(fileUri);
            StorageReference storageRef = storage.getReference().child("imagenes/" + nombreArchivo);
            UploadTask uploadTask = storageRef.putStream(stream);

            uploadTask.addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(onSuccess)
            ).addOnFailureListener(onFailure);
        } catch (Exception e) {
            onFailure.onFailure(e);
        }
    }

    // Metodo para obtener la url de descarga
    public void obtenerArchivo(String nombreArchivo, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
        StorageReference storageRef = storage.getReference().child("imagenes/" + nombreArchivo);
        storageRef.getDownloadUrl().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }
}