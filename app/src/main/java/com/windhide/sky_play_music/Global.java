package com.windhide.sky_play_music;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Global {
    private static Toast currentToast = null; // 全局变量，用于保存当前显示的 Toast
    public static long lastEventTime = System.currentTimeMillis();
    public static List<PointF> clickCoordinates = new ArrayList<>();
    public static View floatingView;

    public static void sendMesseage(Context context,String msg){
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context,
                msg,
                Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
