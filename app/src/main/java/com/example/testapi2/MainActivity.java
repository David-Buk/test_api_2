package com.example.testapi2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadImageToFirebase("https://calm.onelink.me/GyG4/o9tukrg6");
    }

    private void uploadImageToFirebase(String filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(filePath));
        StorageReference imageRef = storageRef.child("images/" + file.getLastPathSegment());

        UploadTask uploadTask = imageRef.putFile(file);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                detectAge(uri.toString());
            });
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void detectAge(String imageUrl) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"url\":\"" + imageUrl + "\"}");
            Request request = new Request.Builder()
                    .url("https://age-detector.p.rapidapi.com/age-detection")
                    .post(body)
                    .addHeader("x-rapidapi-key", "b15b1b33a7msh13e4ac4017ae4a9p1f4deajsn842ee3d5c57a")
                    .addHeader("x-rapidapi-host", "age-detector.p.rapidapi.com")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-RapidAPI-Mock-Response", "200")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse the response to get the age information
                    // Update UI or perform further actions based on the age information
                } else {
                    // Handle unsuccessful response
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
