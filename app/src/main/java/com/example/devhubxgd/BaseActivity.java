package com.example.devhubxgd;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBar();
    }
    //禁止系统文字大小影响app的字体
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;

    }
    private void initSystemBar() {
        Window window = getWindow();
        if(isFitSystemBar()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getSystemBarColor());
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//大于等于4.4
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            /**
             * 可不要半透明效果
             * window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
             * getWindow().setStatusBarColor(Color.TRANSPARENT);
             */
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//大于等于5.0
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getSystemBarColor());// SDK21
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//需要动态添加view来达到改变系统栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                View mTopView = new View(this);
                int statusBarHeight = AppTools.getStatusHeight(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,statusBarHeight);
                params.gravity = Gravity.TOP;
                mTopView.setBackgroundColor(getSystemBarColor());
                ((ViewGroup)window.getDecorView()).addView(mTopView,params);
                ((ViewGroup.MarginLayoutParams) findViewById(android.R.id.content).getLayoutParams()).topMargin = statusBarHeight;
            }
        }
        setStatusDarkBar(AppTools.isLightColor(getSystemBarColor()));
    }

    protected int getSystemBarColor(){
        return 0xffffff;
    }

    /**
     * @param isDark 状态栏文字是否深色
     */
    protected void setStatusDarkBar(boolean isDark) {
        //设置状态栏颜色为深色
        DeviceUtils.setStatusBarDarkMode(this, isDark);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            View view = window.getDecorView();
            int systemUiVisibility = view.getSystemUiVisibility();
            if (isDark) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                systemUiVisibility |=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                view.setSystemUiVisibility(systemUiVisibility);
            } else {
                systemUiVisibility &=~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                view.setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    /**
     * 内容是否延伸到系统状态栏
     * @return
     */
    protected boolean isFitSystemBar(){
        return false;
    }
}
