package com.example.zohaibsiddique.praytimecalculator;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
