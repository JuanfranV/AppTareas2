package com.example.proyectotareas.caracters;
import android.util.Log;



public class AppLoger {

        private static final String GLOBAL_TAG = "AppTareas";

        public static void i(String tag, String msg) {
            Log.i(GLOBAL_TAG, tag + " → " + msg);
        }

        public static void d(String tag, String msg) {
            Log.d(GLOBAL_TAG, tag + " → " + msg);
        }

        public static void w(String tag, String msg) {
            Log.w(GLOBAL_TAG, tag + " → " + msg);
        }

        public static void e(String tag, String msg, Throwable t) {
            Log.e(GLOBAL_TAG, tag + " → " + msg, t);
        }


}
