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
package com.android.launcher3.search;

import android.content.Intent;

import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.util.ComponentKey;

public class AppItemInfoWithIcon extends ItemInfoWithIcon {

    public Intent mIntent;
    public ComponentKey mComponentKey;

    public AppItemInfoWithIcon(ComponentKey componentKey) {
        mComponentKey = componentKey;
        mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.setComponent(componentKey.componentName);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        user = componentKey.user;
    }

    public Intent getIntent() {
        return mIntent;
    }

    @Override
    public AppItemInfoWithIcon clone() {
        return new AppItemInfoWithIcon(mComponentKey);
    };
}
