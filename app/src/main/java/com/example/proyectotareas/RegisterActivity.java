package com.example.proyectotareas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectotareas.caracters.ApiClient;
import com.example.proyectotareas.caracters.ApiService;
import com.example.proyectotareas.model.UsuarioModel;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edTEUser;
    EditText edTEPass;
    EditText edTEConfirm;
    private Button buttonCreate;
    private Button buttonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edTEUser = findViewById(R.id.etUserReg);
        edTEPass = findViewById(R.id.etPassReg);
        edTEConfirm = findViewById(R.id.etPassConfirma);

        edTEPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edTEConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonCreate = findViewById(R.id.btnCrearCuenta);
        buttonBack = findViewById(R.id.btnVolver);

        buttonCreate.setOnClickListener(v -> doRegister());
        buttonBack.setOnClickListener(v -> finish());
    }
    private void doRegister() {

        String user = edTEUser.getText().toString().trim();
        String pass = edTEPass.getText().toString().trim();
        String confirm = edTEConfirm.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user.length() < 3 || pass.length() < 6) {
            Toast.makeText(this, "El usuario debe tener mínimo 3 chars y la contraseña 6", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        UsuarioModel usuario = new UsuarioModel(user, pass);

        api.registrar(usuario).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mensaje = response.body().get("mensaje");
                Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
