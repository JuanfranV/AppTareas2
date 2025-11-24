package com.example.proyectotareas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectotareas.caracters.ApiClient;
import com.example.proyectotareas.caracters.ApiService;
import com.example.proyectotareas.model.UsuarioModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edTEUser;
    private EditText edTEPass;
    private Button buttonLogin;
    private Button buttonRegister;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        edTEUser = findViewById(R.id.etUser);
        edTEPass = findViewById(R.id.etPassword);
        edTEPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonLogin = findViewById(R.id.btnLogin);
        buttonRegister = findViewById(R.id.btnRegister);

        buttonLogin.setOnClickListener(v -> doLogin());

        buttonRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });


    }

    private void doLogin() {
        String user = edTEUser.getText().toString().trim();
        String pass = edTEPass.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        UsuarioModel usuario = new UsuarioModel(user, pass);

        api.login(usuario).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                String r = response.body();

                if ("Login exitoso".equals(r)) {

                    api.getUsuario(user).enqueue(new Callback<UsuarioModel>() {
                        @Override
                        public void onResponse(Call<UsuarioModel> call, Response<UsuarioModel> response) {

                            if (!response.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Error obteniendo usuario", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            UsuarioModel u = response.body();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("userId", u.getId());
                            i.putExtra("username", u.getUsername());
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<UsuarioModel> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Error obteniendo datos", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}