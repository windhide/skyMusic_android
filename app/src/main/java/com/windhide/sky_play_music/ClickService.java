package com.windhide.sky_play_music;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

public class ClickService extends AccessibilityService  {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }
    private static final int MAX_CLICKS = 15;
    private int clickCount = 0;
    public static boolean isRecording = false;
    @Override
    public void onInterrupt() {
        // 必须实现此方法，用于处理服务被中断时的逻辑
    }

    /**
     * 模拟屏幕上的点击或长按
     *
     * @param x            点击的横坐标
     * @param y            点击的纵坐标
     * @param sustainTime  点击的持续时间，单位为毫秒
     */
    public void clickOnScreen(int x, int y, long sustainTime) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        // 使用 sustainTime 参数控制手势持续时间
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, sustainTime));
        GestureDescription gesture = gestureBuilder.build();

        boolean result = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d("AccessibilityService", "Click completed at: (" + x + ", " + y + "), Duration: " + sustainTime + "ms");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d("AccessibilityService", "Click cancelled at: (" + x + ", " + y + ")");
            }
        }, null);

        if (!result) {
            Log.e("AccessibilityService", "Failed to dispatch gesture!");
        }
    }

    /**
     * 模拟同时点击多个屏幕位置，并控制长按时间
     *
     * @param positions    点击位置数组，每个元素是一个 {x, y} 的坐标
     * @param sustainTime  持续时间，单位为毫秒
     */
    public void multiClickOnScreen(int[][] positions, long sustainTime) {
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();

        for (int[] position : positions) {
            int x = position[0];
            int y = position[1];

            // 创建一个路径，表示从点(x, y)开始点击
            Path path = new Path();
            path.moveTo(x, y);

            // 将路径添加到手势中，持续时间由 sustainTime 控制
            GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, sustainTime);
            gestureBuilder.addStroke(stroke);
        }

        // 构建并执行手势
        GestureDescription gesture = gestureBuilder.build();
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d("MultiClick", "Multiple clicks with sustain time " + sustainTime + "ms executed successfully!");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e("MultiClick", "Gesture cancelled!");
            }
        }, null);
    }


    public String onTouchEvent(MotionEvent event) {

        if (isRecording && event.getAction() == MotionEvent.ACTION_DOWN && clickCount < MAX_CLICKS) {
            Global.clickCoordinates.add(new PointF(event.getRawX(), event.getRawY()));
            clickCount++;
            if (clickCount >= MAX_CLICKS) {
                isRecording = false; // 停止记录
                clickCount = 0;
                return "记录点" + 15 + ": (" + event.getRawX() + ", " + event.getRawY() + ")";
            }
            return "记录点" + clickCount + ": (" + event.getRawX() + ", " + event.getRawY() + ")";
        }
        return "false";
    }
}
