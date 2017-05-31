package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;

import java.util.HashSet;

public class AppFilter {
    private final HashSet mHidden;

    private String[] FILTERED_APPS = {
        "com.google.android.googlequicksearchbox/.VoiceSearchActivity",
        "com.google.android.apps.wallpaper/.picker.CategoryPickerActivity",
        "com.google.android.launcher/com.google.android.launcher.StubApp",
    };

    public AppFilter() {
        mHidden = new HashSet();
        for (String pkg: FILTERED_APPS) {
            mHidden.add(ComponentName.unflattenFromString(pkg));
        }
    }

    public static AppFilter newInstance(Context context) {
        return Utilities.getOverrideObject(AppFilter.class, context, R.string.app_filter_class);
    }

    public boolean shouldShowApp(ComponentName app) {
        return !mHidden.contains(app);
    }
}
