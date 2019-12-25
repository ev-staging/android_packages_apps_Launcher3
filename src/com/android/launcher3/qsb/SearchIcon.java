/*
 * Copyright (C) 2019 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.qsb;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.service.voice.VoiceInteractionSession;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.searchlauncher.SearchLauncherCallbacks;

import com.android.systemui.shared.system.ActivityManagerWrapper;

import static com.android.quickstep.inputconsumers.AssistantTouchConsumer.OPA_BUNDLE_TRIGGER;
import static com.android.quickstep.inputconsumers.AssistantTouchConsumer.INVOCATION_TYPE_KEY;

public class SearchIcon extends View implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String OPA_FEATURES_KEY = "opa_enabled";

    // From //java/com/google/android/apps/gsa/assistant/shared/proto/opa_trigger.proto.
    private static final int OPA_BUNDLE_TRIGGER_DIAG_ASSISTANT = 37;
    private static final int INVOCATION_TYPE_ASSISTANT = 4;

    private Context mContext;
    private SharedPreferences mDevicePreferences;
    private Drawable mIcon;
    private boolean mOpaEnabled;

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private int mWidth;
    private int mHeight;

    public SearchIcon(Context context, AttributeSet attributeSet, int res) {
        super(context, attributeSet, res);
        mContext = context;

        mWidth = getWidth() / 2;
        mHeight = getHeight() / 2;

        mDevicePreferences = Utilities.getDevicePrefs(context);

        mOpaEnabled = mDevicePreferences.getBoolean(OPA_FEATURES_KEY, true);
        mIcon = mContext.getDrawable(mOpaEnabled ? R.drawable.ic_qsb_assist : R.drawable.ic_qsb_mic);
        if (mIcon != null) {
            mIntrinsicWidth = mIcon.getIntrinsicWidth() / 2;
            mIntrinsicHeight = mIcon.getIntrinsicHeight() / 2;
            updateBounds();
        }

        setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                startService(view);
            }
        });
    }

    public SearchIcon(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SearchIcon(Context context) {
        this(context, null, 0);
    }

    public void startService(View view) {
        if (mOpaEnabled) {
            String voiceInteraction = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.VOICE_INTERACTION_SERVICE);
            String packageName;
            if (!TextUtils.isEmpty(voiceInteraction)) {
                packageName = ComponentName.unflattenFromString(voiceInteraction).getPackageName();
            } else {
                ResolveInfo activity = mContext.getPackageManager().resolveActivity(new Intent(Intent.ACTION_ASSIST), 65536);
                packageName = activity != null ? activity.resolvePackageName : "";
            }

            if (!TextUtils.isEmpty(packageName) &&
                    SearchLauncherCallbacks.SEARCH_PACKAGE.equals(packageName)) {
                Bundle args = new Bundle();
                args.putInt(OPA_BUNDLE_TRIGGER, OPA_BUNDLE_TRIGGER_DIAG_ASSISTANT);
                args.putInt(INVOCATION_TYPE_KEY, INVOCATION_TYPE_ASSISTANT);
                if (ActivityManagerWrapper.getInstance().showVoiceSession(null, args,
                        VoiceInteractionSession.SHOW_WITH_ASSIST | VoiceInteractionSession.SHOW_SOURCE_ASSIST_GESTURE)) {
                    return;
                }
            }
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VOICE_ASSIST);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setPackage(SearchLauncherCallbacks.SEARCH_PACKAGE);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            ComponentName searchComponentName = new ComponentName(SearchLauncherCallbacks.SEARCH_PACKAGE, ".SearchActivity");
            LauncherAppsCompat.getInstance(mContext).showAppDetailsForProfile(searchComponentName, Process.myUserHandle(), null, null);
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDevicePreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetachedFromWindow() {
        mDevicePreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mIcon != null) {
            mIcon.draw(canvas);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (OPA_FEATURES_KEY.equals(key)) {
            mOpaEnabled = prefs.getBoolean(OPA_FEATURES_KEY, true);
            Drawable newIcon = mContext.getDrawable(mOpaEnabled ? R.drawable.ic_qsb_assist : R.drawable.ic_qsb_mic);
            if (mIcon != newIcon) {
                mIcon = newIcon;
                mIntrinsicWidth = mIcon.getIntrinsicWidth() / 2;
                mIntrinsicHeight = mIcon.getIntrinsicHeight() / 2;
                updateBounds();
            }
            newIcon = null;
            invalidate();
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth() / 2;
        mHeight = getHeight() / 2;
        if (mIcon != null) {
            updateBounds();
        }
    }

    private void updateBounds() {
        final int top = mHeight - mIntrinsicHeight;
        final int bottom = mHeight + mIntrinsicHeight;
        final int start = mWidth - mIntrinsicWidth;
        final int end = mWidth + mIntrinsicWidth;
        mIcon.setBounds(start, top, end, bottom);
    }
}
