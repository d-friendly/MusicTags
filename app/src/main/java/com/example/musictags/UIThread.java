package com.example.musictags;

import android.os.Handler;
import android.os.Looper;

public class UIThread {

        public static void runOnUiThread(Runnable runnable){
            final Handler UIHandler = new Handler(Looper.getMainLooper());
            UIHandler.post(runnable);
        }
    }



