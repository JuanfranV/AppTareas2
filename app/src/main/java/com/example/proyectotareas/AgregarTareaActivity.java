package com.example.proyectotareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectotareas.caracters.AnalyticsHelper;


public class AgregarTareaActivity extends AppCompatActivity {

    EditText edTeTitulo;
    EditText edTeDescripcion;
    CheckBox chBoPendiente;
    CheckBox chBoCompletado;
    Button buttonGuardar;
    Button buttonCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edTeTitulo = findViewById(R.id.edTeTitulo);
        edTeDescripcion = findViewById(R.id.edTeDescripcion);
        chBoCompletado = findViewById(R.id.chBoCompletado);
        chBoPendiente = findViewById(R.id.chBoPendiente);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonCancelar = findViewById(R.id.buttonCancelar);

        chBoPendiente.setChecked(true);


        chBoCompletado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chBoPendiente.setChecked(false);
            }
        });

        chBoPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chBoCompletado.setChecked(false);
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = edTeTitulo.getText().toString();
                String descripcion = edTeDescripcion.getText().toString();
                String estado = chBoCompletado.isChecked() ? "Completado" : chBoPendiente.isChecked() ? "Pendiente": "Ninguno";

                Intent intent = new Intent();
                intent.putExtra("titulo", titulo);
                intent.putExtra("descripcion", descripcion);
                intent.putExtra("estado", estado);
                setResult(RESULT_OK, intent);
                AnalyticsHelper.logCreateTask(titulo);
                finish();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });



    }
}