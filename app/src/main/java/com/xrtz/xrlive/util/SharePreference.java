package com.xrtz.xrlive.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存工具类
 */
public class SharePreference {
    public static void putString(Context context,String name,String value){
        SharedPreferences.Editor editor=context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).edit();
        editor.putString(name,value);
        editor.commit();
    }
    public static String getString(Context context,String name){
        return context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).getString(name,null);
    }
    public static void putInt(Context context,String name,int value){
        SharedPreferences.Editor editor=context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).edit();
        editor.putInt(name,value);
        editor.commit();
    }
    public static  int getInt(Context context,String name){
        return context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).getInt(name,0);
    }
    public static void putBoolean(Context context,String name,boolean value){
        SharedPreferences.Editor editor=context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).edit();
        editor.putBoolean(name,value);
        editor.commit();
    }
    public static  boolean getBoolean(Context context,String name){
        return context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).getBoolean(name,false);
    }

    public static void putLong(Context context,String name,long value){
        SharedPreferences.Editor editor=context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).edit();
        editor.putLong(name,value);
        editor.commit();
    }
    public static  Long getLong(Context context,String name){
        return context.getSharedPreferences("XRLive",Context.MODE_PRIVATE).getLong(name,1l);
    }
}
