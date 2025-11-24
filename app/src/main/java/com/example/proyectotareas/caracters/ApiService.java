package com.example.proyectotareas.caracters;

import com.example.proyectotareas.model.UsuarioModel;
import com.example.proyectotareas.model.UsuarioResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("registrar")
    Call<Map<String, String>> registrar(@Body UsuarioModel user);

    @POST("login")
    Call<UsuarioResponse> login(@Body UsuarioModel user);

    @GET("{username}")
    Call<UsuarioResponse> getUsuario(@Path("username") String username);

}
