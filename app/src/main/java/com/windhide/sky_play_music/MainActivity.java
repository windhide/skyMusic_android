package com.windhide.sky_play_music;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean haveOverlay = PermissionUtils.hasOverlayPermission(MainActivity.this);
        boolean haveAccessibility = PermissionUtils.isAccessibilityServiceEnabled(this, ClickService.class);
        CheckBox step1_check = findViewById(R.id.step1_check);
        CheckBox step2_check = findViewById(R.id.step2_check);

        step1_check.setChecked(haveOverlay);
        step2_check.setChecked(haveAccessibility);


        Button step1_button = findViewById(R.id.step1_button);
        step1_button.setOnClickListener((v)->{
            if(!PermissionUtils.hasOverlayPermission(MainActivity.this)){
                Global.sendMesseage(MainActivity.this, "没有悬浮窗权限，前去打开吧");
                PermissionUtils.requestOverlayPermission(MainActivity.this);
            }else{
                Global.sendMesseage(MainActivity.this, "已有悬浮窗权限");
            }
        });

        Button step2_button = findViewById(R.id.step2_button);
        step2_button.setOnClickListener((v)->{
            if(!PermissionUtils.isAccessibilityServiceEnabled(this, ClickService.class)) {
                Global.sendMesseage(MainActivity.this, "没有无障碍功能哦，前去打开吧");
                PermissionUtils.requestAccessibilityPermission(MainActivity.this);
            }else{
                Global.sendMesseage(MainActivity.this, "已有无障碍功能权限");
            }
        });

        Button step3_button = findViewById(R.id.step3_button);
        step3_button.setOnClickListener((v)->{
            if (!PermissionUtils.hasOverlayPermission(MainActivity.this) ||
                    !PermissionUtils.isAccessibilityServiceEnabled(this, ClickService.class)) {
                Global.sendMesseage(MainActivity.this, "先获取完所有权限吧~");
                return;
            }
            FloatingWindowManager floatingWindowManager = new FloatingWindowManager(this);
            floatingWindowManager.showFloatingWindow();
            Global.sendMesseage(MainActivity.this, "所有权限已获取，开始使用！");
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 返回主界面时，动态更新权限状态和 UI
        boolean haveOverlay = PermissionUtils.hasOverlayPermission(MainActivity.this);
        boolean haveAccessibility = PermissionUtils.isAccessibilityServiceEnabled(this, ClickService.class);

        CheckBox step1_check = findViewById(R.id.step1_check);
        CheckBox step2_check = findViewById(R.id.step2_check);
        step1_check.setChecked(haveOverlay); // 更新 CheckBox 状态
        step2_check.setChecked(haveAccessibility); // 更新 CheckBox 状态
    }
}