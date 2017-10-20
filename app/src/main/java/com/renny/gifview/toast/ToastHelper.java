package com.renny.gifview.toast;

import android.content.Context;

/**
 * toast帮助类
 */
public class ToastHelper {
    private static AppToast appToast;

    //静态工厂方法
    public static AppToast getInstance(Context context) {
        if (appToast == null) {
            appToast = new AppToast(context);
        }
        return appToast;
    }

    public static void makeToast(int textId) {
        appToast.makeToast(textId);
    }

    public static void makeToast(String text) {
        appToast.makeToast(text);
    }



}