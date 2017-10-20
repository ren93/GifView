package com.renny.gifview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.renny.gifview.toast.CustomToast;
import com.renny.gifview.toast.IToast;
import com.renny.gifview.toast.SystemToast;
import com.renny.gifview.toast.ToastCompat;


public class AppToast {
    private boolean isNotificationEnabled;

    public AppToast(Context context) {
        this.context = context.getApplicationContext();
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        isNotificationEnabled = ToastCompat.isNotificationEnabled(context);
    }

    /**
     * application context
     */
    private Context context;
    private IToast toast = null;
    private IToast imgToast = null;
    private View textLayout = null;
    private View textImgLayout = null;
    private WindowManager windowManager;

    private IToast getToast() {
        cancelToast();
        if (!isNotificationEnabled) {
            toast = new CustomToast(context);
        } else {
            toast = new SystemToast(context);
        }
        return toast;
    }

    private IToast getImgToast() {
        cancelImgToast();
        if (!isNotificationEnabled) {
            imgToast = new CustomToast(context);
        } else {
            imgToast = new SystemToast(context);
        }
        return imgToast;
    }

    private synchronized View makeTextView(String text) {
        if (textLayout == null) {
            textLayout = LayoutInflater.from(context).inflate(R.layout.dialog_toast, null);
        }

        if (textLayout.getParent() != null) {
            windowManager.removeView(textLayout);
        }

        TextView mText = (TextView) textLayout.findViewById(R.id.toast_text);
        mText.setText(text);
        return textLayout;
    }


    public IToast makeToast_(String text) {
        IToast toast = getToast();

        View layout = makeTextView(text);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 60);
        toast.setDuration(Toast.LENGTH_SHORT);

        toast.show();
        return toast;
    }

    public void makeToast(final String text) {

        makeToast_(text);

    }

    public void makeToast(int text) {
        makeToast(context.getString(text));
    }


    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        textLayout = null;
    }

    public void cancelImgToast() {
        if (imgToast != null) {
            imgToast.cancel();
            imgToast = null;
        }
        textImgLayout = null;
    }
}
