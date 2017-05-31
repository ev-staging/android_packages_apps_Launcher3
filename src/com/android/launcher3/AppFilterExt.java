package com.android.launcher3;

import android.content.ComponentName;

import java.util.HashSet;

public class AppFilterExt extends AppFilter
{
    private final HashSet mHidden;

    private String[] FILTERED_APPS = {
        "com.google.android.googlequicksearchbox/.VoiceSearchActivity",
        "com.google.android.apps.wallpaper/.picker.CategoryPickerActivity",
        "com.google.android.launcher/com.google.android.launcher.StubApp",
    };

    public AppFilterExt() {
        mHidden = new HashSet();
        for (String pkg: FILTERED_APPS) {
            mHidden.add(ComponentName.unflattenFromString(pkg));
        }
    }

    public boolean shouldShowApp(final ComponentName componentName) {
        return !mHidden.contains(componentName);
    }
}
