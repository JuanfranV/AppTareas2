package com.example.proyectotareas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectotareas.caracters.AnalyticsHelper;
import com.example.proyectotareas.caracters.ApiClient;
import com.example.proyectotareas.caracters.ApiService;
import com.example.proyectotareas.model.UsuarioModel;
import com.example.proyectotareas.model.UsuarioResponse;
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

        api.login(usuario).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {

                AnalyticsHelper.logLogin(user);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    return;
                }

                UsuarioResponse data = response.body();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("username", data.getUsername());
                i.putExtra("userId", data.getId());
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                AnalyticsHelper.logLoginFailed(t.getMessage());
            }

        });
    }
}