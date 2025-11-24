package com.example.proyectotareas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectotareas.caracters.MyApp;

public class fotoActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private ImageView imViFoto;

    Button buttonElegirFoto;
    Button buttonEstablecer;
    Button buttonCancelar;

    private Uri uriSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_foto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imViFoto = findViewById(R.id.imViFoto);
        buttonElegirFoto = findViewById(R.id.buttonElegirFoto);
        buttonEstablecer = findViewById(R.id.buttonEstablecer);
        buttonCancelar = findViewById(R.id.buttonCancelar);

        buttonElegirFoto.setOnClickListener(v -> {
        verificarPermisosYSeleccionarImagen();
        });

        buttonCancelar.setOnClickListener(v -> {
            finish();
        });

        buttonEstablecer.setOnClickListener(v -> {
            if (uriSeleccionada != null) {
                Intent intent = new Intent(fotoActivity.this, MainActivity.class);
                intent.putExtra("imagenUri", uriSeleccionada.toString());
                startActivity(intent);
            }
        });



    }

    private void verificarPermisosYSeleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_CODE);
            } else {
                abrirGaleria();
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        } else {
            abrirGaleria();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriSeleccionada = data.getData();
            imViFoto.setImageURI(uriSeleccionada);

            Bundle params = new Bundle();
            params.putString("uri", uriSeleccionada.toString());
            MyApp.logEvent("photo_selected", params, this);

        }
    }
}