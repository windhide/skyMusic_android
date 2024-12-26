package com.windhide.sky_play_music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class FloatingWindowManager {

    private final Context context;
    private final WindowManager windowManager;

    private View childView;
    private boolean isDragged = false;

    public FloatingWindowManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    // 显示悬浮窗
    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    public void showFloatingWindow() {
        if (Global.floatingView != null) {
            return; // 已经显示悬浮窗
        }

        // 创建悬浮窗布局
        Global.floatingView = LayoutInflater.from(context).inflate(R.layout.floating_window_layout, null);
        childView = Global.floatingView.findViewById(R.id.child_layout);
        Button setKeyButton = childView.findViewById(R.id.set_key_button);
        Button closeButton = childView.findViewById(R.id.close_button);


        // 配置悬浮窗参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START; // 悬浮窗初始位置
        params.x = 0; // 初始 X 坐标
        params.y = 0; // 初始 Y 坐标

        // 添加悬浮窗到窗口
        windowManager.addView(Global.floatingView, params);

        // 获取悬浮窗内的按钮和 NavigationView
        ImageButton suspendedButton = Global.floatingView.findViewById(R.id.suspended_button);
        ClickService clickService = new ClickService();
        setKeyButton.setOnClickListener(v->{
            Global.sendMesseage(context, "开始记录");
            Global.clickCoordinates = new ArrayList<>();
            ClickService.isRecording = true;
            // 设置 childView 的宽度和高度
            ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
            layoutParams.width = 5000;
            layoutParams.height = 5000;
            childView.setLayoutParams(layoutParams);
        });

        closeButton.setOnClickListener(v->{
            childView.setVisibility(View.GONE); // 隐藏菜单
        });


        // 监听触摸事件，记录点击
        childView.setOnTouchListener((v, event) -> {
            if (ClickService.isRecording) {
                String msgText = clickService.onTouchEvent(event);
                if (!"false".equals(msgText)){
                    Global.sendMesseage(context, msgText);
                }
                if(msgText.contains("记录点15")){
                    ViewGroup.LayoutParams layoutParams = childView.getLayoutParams(); // 转换 dp 到 px
                    int widthInPx = (int) (411 * context.getResources().getDisplayMetrics().density);
                    int heightInPx = (int) (291 * context.getResources().getDisplayMetrics().density);
                    layoutParams.width = widthInPx;
                    layoutParams.height = heightInPx;
                    childView.setLayoutParams(layoutParams);
                }
            }
            return true;
        });


        // 设置悬浮窗按钮的点击事件
        suspendedButton.setOnClickListener(v -> {
            if (childView.getVisibility() == View.GONE) {
                childView.setVisibility(View.VISIBLE); // 显示菜单
            } else {
                childView.setVisibility(View.GONE); // 隐藏菜单
            }
        });

        // 设置拖动事件
        suspendedButton.setOnTouchListener(new View.OnTouchListener() {
            private float initialX, initialY, initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragged = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - initialTouchX;
                        float dy = event.getRawY() - initialTouchY;
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                            isDragged = true;
                        }
                        params.x = (int) (initialX + dx);
                        params.y = (int) (initialY + dy);
                        windowManager.updateViewLayout(Global.floatingView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!isDragged) {
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });
    }
}
