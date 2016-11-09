package com.xrtz.xrlive.application;

import android.app.Application;
import android.os.Bundle;

import com.xrtz.xrlive.bean.User;
import com.xrtz.xrlive.handler.CrashHandler;

/**
 * Created by Administrator on 2016/11/2.
 */

public class LiveApplication extends Application {
    private User user;
    static LiveApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static LiveApplication newInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
