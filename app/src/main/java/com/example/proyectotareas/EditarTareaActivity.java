package com.example.proyectotareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectotareas.caracters.AnalyticsHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class EditarTareaActivity extends AppCompatActivity {

    TextView teViTitulo;
    TextView teViDescripcion;
    TextView teViEstado;
    CheckBox chBoCompletado;
    CheckBox chBoPendiente;
    Button buttonGuardar;
    Button buttonCancelar;
    Button buttonBorrar;
    EditText edTeTitulo;
    EditText edTeDescripcion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        teViTitulo = findViewById(R.id.teViTitulo);
        teViDescripcion = findViewById(R.id.teViDescripcion);
        teViEstado = findViewById(R.id.teViEstado);
        chBoCompletado = findViewById(R.id.chBoCompletado);
        chBoPendiente = findViewById(R.id.chBoPendiente);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        buttonBorrar = findViewById(R.id.buttonBorrar);

        chBoCompletado.setOnClickListener(view -> {
            chBoPendiente.setChecked(false);
        });

        chBoPendiente.setOnClickListener(view -> {
            chBoCompletado.setChecked(false);
        });

            Intent intent = getIntent();
            edTeTitulo = findViewById(R.id.edTeTitulo);
            edTeDescripcion = findViewById(R.id.edTeDescripcion);

            edTeTitulo.setText(intent.getStringExtra("titulo"));
            edTeDescripcion.setText(intent.getStringExtra("descripcion"));
            String estado = intent.getStringExtra("estado");
            if ("Pendiente".equals(estado)) {
                chBoPendiente.setChecked(true);
            } else if ("Completado".equals(estado)) {
                chBoCompletado.setChecked(true);
            }
    
            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String titulo = edTeTitulo.getText().toString();
                    String descripcion = edTeDescripcion.getText().toString();
                    String estado = chBoCompletado.isChecked() ? "Completado" : chBoPendiente.isChecked() ? "Pendiente" : "Ninguno";
    
                    Intent resultadoIntent = new Intent();
                    resultadoIntent.putExtra("titulo", titulo);
                    resultadoIntent.putExtra("descripcion", descripcion);
                    resultadoIntent.putExtra("estado", estado);
                    resultadoIntent.putExtra("posicion", getIntent().getIntExtra("posicion", -1));
                    setResult(RESULT_OK, resultadoIntent);
                    AnalyticsHelper.logCompleteTask(teViTitulo.getText().toString());
                    finish();
                }
            });
    
            buttonCancelar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    finish();
                }
            });
    
            buttonBorrar.setOnClickListener(view -> {
                Intent resultadoIntent = new Intent();
                resultadoIntent.putExtra("accion", "eliminar");
                resultadoIntent.putExtra("posicion", getIntent().getIntExtra("posicion", -1));
                
                setResult(RESULT_OK, resultadoIntent);
                AnalyticsHelper.logDeleteTask(teViTitulo.getText().toString());
                finish();
    
            });



    }



}