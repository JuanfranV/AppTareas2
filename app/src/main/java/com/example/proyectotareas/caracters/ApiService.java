package com.example.proyectotareas.caracters;

import com.example.proyectotareas.model.UsuarioModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("registrar")
    Call<String> registrar(@Body UsuarioModel user);

    @POST("login")
    Call<String> login(@Body UsuarioModel user);

    @GET("{username}")
    Call<UsuarioModel> getUsuario(@Path("username") String username);

}