package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HomeKeyWatcher {

    private Context mContext;
    private IntentFilter mIntentFilter;
    private OnHomePressedListener mOnHomePressedListener;
    private HomeRecevier mHomeRecevier;

    public HomeKeyWatcher(Context context) {
        mContext = context;
        mHomeRecevier = new HomeRecevier();
        mIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mOnHomePressedListener = listener;
    }

    public void startWatch() {
        mContext.registerReceiver(mHomeRecevier, mIntentFilter);
    }

    public void stopWatch() {
        mOnHomePressedListener = null;
        if (mHomeRecevier != null) {
            try {
                mContext.unregisterReceiver(mHomeRecevier);
                mHomeRecevier = null;
            } catch (IllegalArgumentException e) {};
        }
    }

    private class HomeRecevier extends BroadcastReceiver {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (mOnHomePressedListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            mOnHomePressedListener.onHomePressed();
                        }
                    }
                }
            }
        }
    }

    public interface OnHomePressedListener {
        void onHomePressed();
    }
}
