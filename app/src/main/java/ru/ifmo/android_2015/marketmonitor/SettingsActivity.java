package ru.ifmo.android_2015.marketmonitor;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.List;

import alarm.UpdateAllTargetsService;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String KEY_PREF_SYNC = "preference_sync";
    private static final String KEY_PREF_SYNC_FREQ = "preference_sync_frequency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.preference_screen);
        //TODO: use fragment

        preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "Preference changed");
                if (key.equals(KEY_PREF_SYNC_FREQ)) {
                    Preference syncPref = findPreference(key);
                    if (syncPref instanceof ListPreference) {
                        ListPreference pref = (ListPreference) syncPref;
                        syncPref.setSummary(pref.getEntry());

                        int interval = Integer.parseInt(
                                sharedPreferences.getString(KEY_PREF_SYNC_FREQ, "30"));
                        setUpAlarm(interval);
                    }
                } else if (key.equals(KEY_PREF_SYNC)) {
                    boolean pref = sharedPreferences.getBoolean(key, false);
                    if (pref) {
                        int interval = Integer.parseInt(
                                sharedPreferences.getString(KEY_PREF_SYNC_FREQ, "30"));
                        setUpAlarm(interval);
                    } else {
                        cancelAlarm();
                    }
                }
            }
        };
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;

    @Override
    protected void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpAlarm(int minutes) {
        Intent serviceIntent = new Intent(this, UpdateAllTargetsService.class);
        PendingIntent pIntent = PendingIntent.getService(this, 1, serviceIntent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                0, 60000 * minutes, pIntent);
    }

    private void cancelAlarm() {
        Intent serviceIntent = new Intent(this, UpdateAllTargetsService.class);
        PendingIntent pIntent = PendingIntent.getService(this, 1, serviceIntent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    private static final String TAG = SettingsActivity.class.getSimpleName();

}
