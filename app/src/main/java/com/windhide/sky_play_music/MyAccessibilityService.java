package com.windhide.sky_play_music;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理接收到的无障碍事件
    }

    @Override
    public void onInterrupt() {
        // 必须实现此方法，用于处理服务被中断时的逻辑
    }
}
