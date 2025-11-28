package com.example.proyectotareas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectotareas.caracters.AnalyticsHelper;
import com.example.proyectotareas.caracters.AppLoger;
import com.example.proyectotareas.caracters.MyApp;
import com.example.proyectotareas.caracters.PhotoStorage;
import com.example.proyectotareas.caracters.tareaAdapter;
import com.example.proyectotareas.model.agregarTareaModel;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button buttonAgregar;
    ArrayList<agregarTareaModel> listaTareas = new ArrayList<>();
    RecyclerView recyclerTareas;
    tareaAdapter adapter;
    ImageView imViFoto;
    public ActivityResultLauncher<Intent> agregarTareaLauncher;
    TextView txtClima;
    String API_KEY = "08279d7fa577b4bc7025a401f440330d";
    private String username;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);

        if (username == null || userId == -1) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imViFoto = findViewById(R.id.imViFoto);

        if (username != null) {
            String foto = PhotoStorage.obtenerFoto(this, username);
            if (foto != null) {
                imViFoto.setImageURI(Uri.parse(foto));
            }
        }



        txtClima = findViewById(R.id.teViClima);
        obtenerClima("Guatemala");

        recyclerTareas = findViewById(R.id.recyclerTareas);
        recyclerTareas.setLayoutManager(new LinearLayoutManager(this));

        buttonAgregar = findViewById(R.id.buttonAñadir);

        adapter = new tareaAdapter(this, listaTareas);
        recyclerTareas.setAdapter(adapter);

        // Carga las tareas desde la API de Render
        obtenerTareasDesdeAPI();


        agregarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String accion = data.getStringExtra("accion");
                        int posicion = data.getIntExtra("posicion", -1);


                        if ("eliminar".equals(accion) && posicion != -1) {
                            agregarTareaModel tarea = listaTareas.get(posicion);

                            // BORRAR TAREA
                            listaTareas.remove(posicion);
                            adapter.notifyItemRemoved(posicion);

                            //LLAMADA A LA API
                            eliminarTareaEnAPI(tarea.getId());

                            MyApp.logEvent("task_deleted", null, this);
                        } else {
                            String titulo = data.getStringExtra("titulo");
                            String descripcion = data.getStringExtra("descripcion");
                            String estado = data.getStringExtra("estado");

                            if (titulo != null && descripcion != null && estado != null) {
                                if (posicion != -1) {

                                    // EDITAR TAREA
                                    MyApp.logEvent("task_edited", null, this);
                                    agregarTareaModel tarea = listaTareas.get(posicion);

                                    //LLAMADA A LA API
                                    actualizarTareaEnAPI(tarea.getId(), titulo, descripcion, estado);

                                    tarea.setTitulo(titulo);
                                    tarea.setDescripcion(descripcion);
                                    tarea.setEstado(estado);
                                    adapter.notifyItemChanged(posicion);
                                }else {
                                    MyApp.logEvent("task_added", null, this);

                                    // NUEVA TAREA
                                    agregarTareaEnAPI(titulo, descripcion, estado);
                                }
                            }
                        }
                    }
                });

        buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AgregarTareaActivity.class);
                agregarTareaLauncher.launch(intent);
            }
        });

        imViFoto.setOnClickListener( view ->{
            Intent intent = new Intent(this, fotoActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userId", userId);
            startActivity(intent);

        });

        String imageUriString = getIntent().getStringExtra("imagenUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            imViFoto.setImageURI(imageUri);
        }




    }

    @Override
    protected void onResume() {
        super.onResume();

        if (username != null) {
            String foto = PhotoStorage.obtenerFoto(this, username);
            if (foto != null) {
                imViFoto.setImageURI(Uri.parse(foto));
            }
        }

        AnalyticsHelper.logListTasks();
    }



    private void obtenerClima(String ciudad) {

        AppLoger.d("MainActivity", "Solicitando clima para ciudad: " + ciudad);

        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + ciudad + "&appid=" + API_KEY + "&units=metric&lang=es";

        RequestQueue queue = Volley.newRequestQueue(this);

        Trace trace = FirebasePerformance.getInstance().newTrace("clima_request");
        trace.start();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temp = main.getDouble("temp");

                        JSONArray weather = response.getJSONArray("weather");
                        String descripcion = weather.getJSONObject(0).getString("description");

                        txtClima.setText("Clima en " + ciudad + ":\n" + temp + "°C, " + descripcion);

                        AppLoger.i("MainActivity", "Clima recibido: " + temp + "°C, " + descripcion);

                        AnalyticsHelper.logApiCallSuccess(url);
                        trace.stop();
                    } catch (JSONException e) {
                        AppLoger.w("MainActivity", "Respuesta inesperada de la API");
                        e.printStackTrace();
                    }
                },
                error -> {
                    txtClima.setText("Error: " + error.getMessage());
                    AnalyticsHelper.logApiCallError(url, error.getMessage());
                    trace.putMetric("errors", 1);
                    trace.stop();

                });
        queue.add(request);

    }

    private void obtenerTareasDesdeAPI() {
        if (userId == -1) {
            Toast.makeText(this, "ID de usuario inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        String url = "https://apitareas-u3nf.onrender.com/api/tareas/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaTareas.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String titulo = obj.getString("titulo");
                            String descripcion = obj.getString("descripcion");
                            String estado = obj.getString("estado");

                            listaTareas.add(new agregarTareaModel(id, titulo, descripcion, estado));

                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error al obtener tareas", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }


    private void agregarTareaEnAPI(String titulo, String descripcion, String estado) {
        String url = "https://apitareas-u3nf.onrender.com/api/tareas?username=" + username;

        JSONObject tarea = new JSONObject();
        try {
            tarea.put("titulo", titulo);
            tarea.put("descripcion", descripcion);
            tarea.put("estado", estado);
        } catch (JSONException e) { return; }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, tarea,
                response -> {
                    Toast.makeText(this, "Tarea añadida", Toast.LENGTH_SHORT).show();
                    obtenerTareasDesdeAPI();
                },
                error -> Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }


    private void eliminarTareaEnAPI(int id) {
        String url = "https://apitareas-u3nf.onrender.com/api/tareas/" + id;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> obtenerTareasDesdeAPI(),
                error -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void actualizarTareaEnAPI(int id, String titulo, String descripcion, String estado) {
        String url = "https://apitareas-u3nf.onrender.com/api/tareas/" + id;

        JSONObject tarea = new JSONObject();
        try {
            tarea.put("titulo", titulo);
            tarea.put("descripcion", descripcion);
            tarea.put("estado", estado);
        } catch (JSONException e) { return; }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, tarea,
                response -> obtenerTareasDesdeAPI(),
                error -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}



