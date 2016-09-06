package com.delelong.baidumaptest.utils;

import android.app.Application;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MyApp extends Application {

    private MyApp myApp;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
    }
    public MyApp getInstance(){
        return myApp;
    }
}
