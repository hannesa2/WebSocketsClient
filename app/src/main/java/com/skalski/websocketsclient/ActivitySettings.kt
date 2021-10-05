package com.skalski.websocketsclient

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import timber.log.Timber

class ActivitySettings : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mFragmentManager = fragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mPrefsFragment = PrefsFragment()
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment)
        mFragmentTransaction.commit()
    }

    class PrefsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings)
        }
    }

    companion object {
        private const val TAG_LOG = "WebSocketsClient"
        private const val TAG_HOSTNAME = "hostname"
        private const val TAG_PORT_NUMBER = "port"
        private const val TAG_TIMEOUT = "timeout"
        private const val TAG_DISABLE_NOTIFICATIONS = "disable_notifications"
        private const val TAG_DISABLE_MULTIPLE_NOTIFICATIONS = "disable_multiple_notifications"
        fun pref_get_hostname(context: Context?): String? {
            val value: String? = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_HOSTNAME, null)
            Timber.i("pref_get_hostname() value: $value")
            return value
        }

        fun prefHostname(context: Context?, value: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(TAG_HOSTNAME, value)
            editor.apply()
            Timber.i("pref_set_hostname() value: $value")
        }

        fun pref_get_port_number(context: Context?): String? {
            val value: String? = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_PORT_NUMBER, null)
            Timber.i("pref_get_port_number() value: $value")
            return value
        }

        fun pref_set_port_number(context: Context?, value: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(TAG_PORT_NUMBER, value)
            editor.apply()
            Timber.i("pref_set_port_number() value: $value")
        }

        fun prefTimeout(context: Context?): String? {
            val value: String? = PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_TIMEOUT, null)
            Timber.i("pref_get_timeout() value: $value")
            return value
        }

        fun pref_set_timeout(context: Context?, value: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(TAG_TIMEOUT, value)
            editor.apply()
            Timber.i("pref_set_timeout() value: $value")
        }

        fun pref_notifications_disabled(context: Context?): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_NOTIFICATIONS, false)
        }

        fun pref_multiple_notifications_disabled(context: Context?): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_MULTIPLE_NOTIFICATIONS, false)
        }
    }
}
