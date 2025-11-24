package com.example.proyectotareas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectotareas.caracters.ApiClient;
import com.example.proyectotareas.caracters.ApiService;
import com.example.proyectotareas.model.UsuarioModel;

import retrofit2.Call;

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

        api.registrar(usuario).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                String r = response.body();

                if ("Usuario creado".equals(r)) {
                    Toast.makeText(RegisterActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, r, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
