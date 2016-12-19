package com.emba.bluetoothdemo;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2016-12-19
 *
 * @desc
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
