package com.delelong.diandian;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

/**
 * Created by Administrator on 2016/9/6.
 */
public class SettingActivity extends BaseActivity{

    ActionBar actionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        setContentView(R.layout.activity_setting);

    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= 21) {
            //用于去除阴影
            actionBar.setElevation(0);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
