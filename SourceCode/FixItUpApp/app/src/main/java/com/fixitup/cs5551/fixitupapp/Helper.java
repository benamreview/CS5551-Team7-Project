package com.fixitup.cs5551.fixitupapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Helper {
    public static void showLog(String message) {
        Log.e("Ketan", "" + message);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
    }
}
