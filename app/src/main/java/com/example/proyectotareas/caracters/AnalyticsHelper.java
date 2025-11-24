package com.example.proyectotareas.caracters;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
public class AnalyticsHelper {

    private static FirebaseAnalytics analytics;

    // inicializar
    public static void init(Context context) {
        if (analytics == null) {
            analytics = FirebaseAnalytics.getInstance(context);

        }
    }

    //login
    public static void logLogin(String username) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        analytics.logEvent("login", bundle);
    }

    //login fallido
    public static void logLoginFailed(String reason) {
        Bundle bundle = new Bundle();
        bundle.putString("error_reason", reason);
        analytics.logEvent("login_failed", bundle);

    }

    //Crear tarea
    public static void logCreateTask(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("task_title", title);
        analytics.logEvent("create_task", bundle);
    }

    //completar tarea
    public static void logCompleteTask(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("task_title", title);
        analytics.logEvent("complete_task", bundle);
    }

    //eliminar tarea
    public static void logDeleteTask(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("task_title", title);
        analytics.logEvent("delete_task", bundle);
    }

    //Api llamada exitosa
    public static void logApiCallSuccess(String endpoint) {
        Bundle bundle = new Bundle();
        bundle.putString("endpoint", endpoint);
        analytics.logEvent("api_call_success", bundle);

    }

    //Api llamada con error
    public static void logApiCallError(String endpoint, String error) {
        Bundle bundle = new Bundle();
        bundle.putString("endpoint", endpoint);
        bundle.putString("error", error);
        analytics.logEvent("api_call_error", bundle);
    }

    //se lista la lista
    public static void logListTasks() {
        analytics.logEvent("list_view", null);
    }
















}
