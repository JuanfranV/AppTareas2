package com.example.proyectotareas.caracters;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa Firebase Analytics una sola vez
        AnalyticsHelper.init(this);
    }

    public static void logEvent(String eventName, Bundle params, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("analytics_enabled", true);

        if (enabled) {
            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
            analytics.logEvent(eventName, params);
        } else {
            Log.d("Analytics", "Evento bloqueado por usuario: " + eventName);
        }
    }

}

