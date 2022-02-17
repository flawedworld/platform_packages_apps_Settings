package com.android.settings.network;

import android.content.Context;

import android.os.UserHandle;
import android.os.UserManager;
import android.os.SystemProperties;

import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class EsimCompatPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, OnResume, Preference.OnPreferenceChangeListener {

    private static final String KEY_ESIM_COMPAT = "esim_compat";
    private static final String PREF_KEY_ESIM_CATEGORY = "esim_category";

    private PreferenceCategory mEsimCategory;
    private ListPreference mEsimCompat;

    public EsimCompatPreferenceController(Context context) {
        super(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mEsimCategory = screen.findPreference(PREF_KEY_ESIM_CATEGORY);
        updatePreferenceState();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_ESIM_COMPAT;
    }

    // TODO: should we use onCreatePreferences() instead?
    private void updatePreferenceState() {
        if (mEsimCategory == null) {
            return;
        }
        mEsimCompat = (ListPreference) mEsimCategory.findPreference(KEY_ESIM_COMPAT);
        mEsimCompat.setValue(Boolean.toString(Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.ESIM_COMPAT, 0) != 0));
    }

    @Override
    public void onResume() {
        updatePreferenceState();
        if (mEsimCompat != null) {
            boolean mode = Boolean.parseBoolean(mEsimCompat.getValue());
            Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.ESIM_COMPAT, (mode) ? 0 : 1);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        if (KEY_ESIM_COMPAT.equals(key)) {
            boolean mode = Boolean.parseBoolean((String) value);
            Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.ESIM_COMPAT, (mode) ? 1 : 0);
        }
        return true;
    }
}
