/*
 * Copyright 2014 Lukasz Skalski <lukasz.skalski@op.pl>
 *
 * This file is part of WebSocketsClient.
 *
 * WebSocketsClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebSocketsClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebSocketsClient.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skalski.websocketsclient;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class ActivitySettings extends Activity {

    private static final String TAG_LOG = "WebSocketsClient";
    private static final String TAG_HOSTNAME = "hostname";
    private static final String TAG_PORT_NUMBER = "port";
    private static final String TAG_TIMEOUT = "timeout";
    private static final String TAG_DISABLE_NOTIFICATIONS = "disable_notifications";
    private static final String TAG_DISABLE_MULTIPLE_NOTIFICATIONS = "disable_multiple_notifications";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    public static String pref_get_hostname(Context context) {
        String value;
        value = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_HOSTNAME, null);
        Log.i(TAG_LOG, "pref_get_hostname() value: " + value);
        return value;
    }

    public static void pref_set_hostname(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_HOSTNAME, value);
        editor.commit();
        Log.i(TAG_LOG, "pref_set_hostname() value: " + value);
    }

    public static String pref_get_port_number(Context context) {
        String value;
        value = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_PORT_NUMBER, null);
        Log.i(TAG_LOG, "pref_get_port_number() value: " + value);
        return value;
    }

    public static void pref_set_port_number(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_PORT_NUMBER, value);
        editor.commit();
        Log.i(TAG_LOG, "pref_set_port_number() value: " + value);
    }

    public static String pref_get_timeout(Context context) {
        String value;
        value = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_TIMEOUT, null);
        Log.i(TAG_LOG, "pref_get_timeout() value: " + value);
        return value;
    }

    public static void pref_set_timeout(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TIMEOUT, value);
        editor.commit();
        Log.i(TAG_LOG, "pref_set_timeout() value: " + value);
    }

    public static Boolean pref_notifications_disabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_NOTIFICATIONS, false);
    }

    public static Boolean pref_multiple_notifications_disabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_MULTIPLE_NOTIFICATIONS, false);
    }
}