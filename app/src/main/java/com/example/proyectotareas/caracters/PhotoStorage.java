package com.example.proyectotareas.caracters;

import android.content.Context;
import android.content.SharedPreferences;

public class PhotoStorage {

    private static final String PREF_NAME = "user_photos";

    public static void guardarFoto(Context context, String username, String uri) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(username, uri).apply();
    }

    public static String obtenerFoto(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(username, null);
    }
}

