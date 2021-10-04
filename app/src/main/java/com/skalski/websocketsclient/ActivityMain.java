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
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.github.johnpersano.supertoasts.util.Wrappers;
import com.skalski.websocketsclient.SecureWebSocktes.WebSocket;
import com.skalski.websocketsclient.SecureWebSocktes.WebSocketConnection;
import com.skalski.websocketsclient.SecureWebSocktes.WebSocketException;
import com.skalski.websocketsclient.SecureWebSocktes.WebSocketOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import de.psdev.licensesdialog.LicensesDialog;

public class ActivityMain extends Activity implements WebSocket.WebSocketConnectionObserver {

    private static final String TAG_LOG = "WebSocketsClient";
    private static final String TAG_JSON_TYPE = "Type";
    private static final String TAG_JSON_MSG = "Message";

    private volatile boolean isConnected = false;
    private WebSocketConnection wsConnection;
    private WebSocketOptions wsOptions;
    private URI wsURI;

    private EditText cmdInput;
    private TextView cmdOutput;
    private CircularProgressButton connectButton;

    private EditText hostname;
    private EditText portNumber;
    private EditText timeout;

    /*
     * ActivityMain - onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Wrappers wrappers = new Wrappers();
        wrappers.add(onClickWrapperExit);
        SuperActivityToast.onRestoreState(savedInstanceState, ActivityMain.this, wrappers);

        hostname = (EditText) findViewById(R.id.hostname);
        portNumber = (EditText) findViewById(R.id.port);
        timeout = (EditText) findViewById(R.id.timeout);

        cmdInput = (EditText) findViewById(R.id.cmdInput);
        cmdOutput = (TextView) findViewById(R.id.cmdOutput);
        connectButton = (CircularProgressButton) findViewById(R.id.btnConnect);

        cmdOutput.setMovementMethod(new ScrollingMovementMethod());
        connectButton.setIndeterminateProgressMode(true);

        /*
         * connectButton - onClickListener()
         */
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectButton.getProgress() == 0) {
                    connectButton.setProgress(50);

                    if ((hostname.getText().toString().equals("")) ||
                            (portNumber.getText().toString().equals("")) ||
                            (timeout.getText().toString().equals(""))) {

                        Log.e(TAG_LOG, "Invalid connection settings");
                        show_info(getResources().getString(R.string.info_msg_1), false);
                        connectButton.setProgress(-1);
                        return;
                    }

                    /* save last settings */
                    ActivitySettings.pref_set_hostname(getBaseContext(), hostname.getText().toString());
                    ActivitySettings.pref_set_port_number(getBaseContext(), portNumber.getText().toString());
                    ActivitySettings.pref_set_timeout(getBaseContext(), timeout.getText().toString());

                    /* connect */
                    if (!wsConnect()) {
                        show_info(getResources().getString(R.string.info_msg_2), false);
                        connectButton.setProgress(-1);
                    }

                } else if (connectButton.getProgress() == -1) {
                    connectButton.setProgress(0);
                }
            }
        });

        /*
         * cmdInput - OnEditorActionListener()
         */
        cmdInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    /* send data to server */
                    wsSend();
                    return true;
                }
                return false;
            }
        });
    }

    /*
     * ActivityMain - onResume()
     */
    @Override
    public void onResume() {
        hostname.setText(ActivitySettings.pref_get_hostname(getBaseContext()));
        portNumber.setText(ActivitySettings.pref_get_port_number(getBaseContext()));
        timeout.setText(ActivitySettings.pref_get_timeout(getBaseContext()));
        super.onResume();
    }

    /*
     * ActivityMain - wsConnect()
     */
    boolean wsConnect() {

        if (!this.isConnected) {

            this.wsConnection = new WebSocketConnection();
            this.wsOptions = new WebSocketOptions();
            wsOptions.setSocketConnectTimeout(Integer.parseInt(timeout.getText().toString()));

            try {
                this.wsURI = new URI("ws://" + hostname.getText().toString() + ":" + portNumber.getText().toString());
                wsConnection.connect(wsURI, this, wsOptions);
            } catch (WebSocketException e) {
                Log.e(TAG_LOG, "Can't connect to server - 'WebSocketException'");
                this.isConnected = false;
                return false;
            } catch (URISyntaxException e1) {
                Log.e(TAG_LOG, "Can't connect to server - 'URISyntaxException'");
                this.isConnected = false;
                return false;
            } catch (Exception ex) {
                Log.e(TAG_LOG, "Can't connect to server - 'Exception'");
                this.isConnected = false;
                return false;
            }

            Log.i(TAG_LOG, "Connected");
            this.isConnected = true;
            return true;
        }

        Log.w(TAG_LOG, "You are already connected to the server");
        return true;
    }

    /*
     * ActivityMain - wsDisconnect()
     */
    void wsDisconnect() {
        if (isConnected) {
            Log.i(TAG_LOG, "Disconnected");
            wsConnection.disconnect();
            connectButton.setProgress(0);
        }
    }

    /*
     * ActivityMain - wsSend()
     */
    void wsSend() {
        if (isConnected) {

            /* send message to the server */
            Log.i(TAG_LOG, "Message has been successfully sent");
            wsConnection.sendTextMessage(cmdInput.getText().toString());
            appendText(cmdOutput, "[CLIENT] " + cmdInput.getText().toString() + "\n", Color.RED);

        } else {

            /* no connection to the server */
            connectButton.setProgress(-1);
            show_info(getResources().getString(R.string.info_msg_2), false);
        }
        cmdInput.getText().clear();
    }

    /*
     * SecureWebSockets - onOpen()
     */
    @Override
    public void onOpen() {

        Log.i(TAG_LOG, "onOpen() - connection opened to: " + wsURI.toString());
        this.isConnected = true;
        connectButton.setProgress(100);
    }

    /*
     * SecureWebSockets - onClose()
     */
    @Override
    public void onClose(WebSocketCloseNotification code, String reason) {

        Log.i(TAG_LOG, "onClose() - " + code.name() + ", " + reason);
        this.isConnected = false;
        connectButton.setProgress(0);
    }

    /*
     * SecureWebSockets - onTextMessage()
     */
    @Override
    public void onTextMessage(String payload) {

        try {

            Log.i(TAG_LOG, "New message from server");
            JSONObject jsonObj = new JSONObject(payload);

            if ((jsonObj.has(TAG_JSON_TYPE)) && (jsonObj.has(TAG_JSON_MSG))) {

                /*
                 * Notification
                 */
                if (jsonObj.getString(TAG_JSON_TYPE).equals("notification")) {

                    if (ActivitySettings.pref_notifications_disabled(getBaseContext())) {

                        Log.i(TAG_LOG, "Notifications are disabled");

                    } else {

                        int notification_id;

                        if (ActivitySettings.pref_multiple_notifications_disabled(getBaseContext()))
                            notification_id = 0;
                        else
                            notification_id = (int) System.currentTimeMillis();

                        /* create new notification */
                        Notification new_notification = new Notification.Builder(this)
                                .setContentTitle(getResources().getString(R.string.app_name))
                                .setContentText(jsonObj.getString(TAG_JSON_MSG))
                                .setSmallIcon(R.drawable.ic_launcher).build();
                        new_notification.defaults |= Notification.DEFAULT_ALL;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(notification_id, new_notification);

                        appendText(cmdOutput, "[SERVER] Asynchronous Notification\n", Color.parseColor("#ff0099cc"));
                    }

                    /*
                     * Standard message
                     */
                } else if (jsonObj.getString(TAG_JSON_TYPE).equals("standard")) {

                    appendText(cmdOutput, "[SERVER] " + jsonObj.getString(TAG_JSON_MSG) + "\n", Color.parseColor("#ff99cc00"));

                    /*
                     * JSON object is not valid
                     */
                } else {
                    show_info(getResources().getString(R.string.info_msg_4), false);
                    Log.e(TAG_LOG, "Received invalid JSON from server");
                }
            }
        } catch (JSONException e) {

            /* JSON object is not valid */
            show_info(getResources().getString(R.string.info_msg_4), false);
            Log.e(TAG_LOG, "Received invalid JSON from server");
        }
    }

    /*
     * SecureWebSockets - onRawTextMessage()
     */
    @Override
    public void onRawTextMessage(byte[] payload) {
        Log.wtf(TAG_LOG, "We didn't expect 'RawTextMessage'");
    }

    /*
     * SecureWebSockets - onBinaryMessage()
     */
    @Override
    public void onBinaryMessage(byte[] payload) {
        Log.wtf(TAG_LOG, "We didn't expect 'BinaryMessage'");
    }

    /*
     * ActivityMain - show_info()
     */
    void show_info(String info, boolean showButton) {

        SuperActivityToast superActivityToast;

        if (showButton) {
            superActivityToast = new SuperActivityToast(ActivityMain.this, SuperToast.Type.BUTTON);
            superActivityToast.setOnClickWrapper(onClickWrapperExit);
            superActivityToast.setButtonIcon(SuperToast.Icon.Dark.EXIT, "Exit");
        } else {
            superActivityToast = new SuperActivityToast(ActivityMain.this, SuperToast.Type.STANDARD);
            superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        }

        superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
        superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
        superActivityToast.setBackground(SuperToast.Background.RED);
        superActivityToast.setText(info);
        superActivityToast.show();
    }

    /*
     * ActivityMain - appendText()
     */
    static void appendText(TextView textView, String text, int textColor) {

        int start;
        int end;

        start = textView.getText().length();
        textView.append(text);
        end = textView.getText().length();

        Spannable spannableText = (Spannable) textView.getText();
        spannableText.setSpan(new ForegroundColorSpan(textColor), start, end, 0);
    }

    /*
     * ActivityMain - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * ActivityMain - onOptionsItemSelected()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* show 'Settings' */
        if (id == R.id.action_settings) {
            Log.i(TAG_LOG, "Starting 'ActivitySettings'");
            Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
            startActivity(intent);
            return true;

            /* show 'Open Source Licenses' */
        } else if (id == R.id.action_licenses) {
            Log.i(TAG_LOG, "Starting 'LicensesDialog'");
            new LicensesDialog(ActivityMain.this, R.raw.notices, false, true).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * ActivityMain - onKeyDown()
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return true;
    }

    /*
     * ActivityMain - onClickWrapperExit()
     */
    OnClickWrapper onClickWrapperExit = new OnClickWrapper("id_exit", new SuperToast.OnClickListener() {
        @Override
        public void onClick(View view, Parcelable token) {
            wsDisconnect();
            finish();
        }
    });

    /*
     * ActivityMain - onBackPressed()
     */
    @Override
    public void onBackPressed() {
        show_info(getResources().getString(R.string.info_msg_3), true);
    }
}