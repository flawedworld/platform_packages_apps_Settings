package com.android.settings.security;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class MaxFaceAttemptsPreferenceController extends AbstractPreferenceController
    implements PreferenceControllerMixin, OnResume,
           Preference.OnPreferenceChangeListener {

    private static final String KEY_MAX_FACE_ATTEMPTS = "max_face_attempts";
    private static final String PREF_KEY_SECURITY_CATEGORY = "security_category";

    private PreferenceCategory mSecurityCategory;
    private boolean mIsAdmin;
    private final UserManager mUm;

    public MaxFaceAttemptsPreferenceController(Context context) {
        super(context);
        mUm = UserManager.get(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSecurityCategory = screen.findPreference(PREF_KEY_SECURITY_CATEGORY);
        updatePreferenceState();
    }

    @Override
    public boolean isAvailable() {
        mIsAdmin = mUm.isAdminUser();
        return mIsAdmin;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_MAX_FACE_ATTEMPTS;
    }

    // TODO: should we use onCreatePreferences() instead?
    private void updatePreferenceState() {
        if (mSecurityCategory == null) {
            return;
        }

        if (mIsAdmin) {
            ListPreference maxFaceAttempts =
                    (ListPreference) mSecurityCategory.findPreference(KEY_MAX_FACE_ATTEMPTS);
            maxFaceAttempts.setValue(Long.toString(Settings.Global.getLong(
                    mContext.getContentResolver(), Settings.Global.MAX_FACE_ATTEMPTS, 0)));
        } else {
            mSecurityCategory.removePreference(
                    mSecurityCategory.findPreference(KEY_MAX_FACE_ATTEMPTS));
        }
    }

    @Override
    public void onResume() {
        updatePreferenceState();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        if (KEY_MAX_FACE_ATTEMPTS.equals(key) && mIsAdmin) {
            long timeout = Long.parseLong((String) value);
            Settings.Global.putLong(mContext.getContentResolver(), Settings.Global.MAX_FACE_ATTEMPTS, timeout);
        }
        return true;
    }
}
