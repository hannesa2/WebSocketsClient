package com.skalski.websocketsclient

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.dd.CircularProgressButton
import com.github.johnpersano.supertoasts.SuperActivityToast
import com.github.johnpersano.supertoasts.SuperToast
import com.github.johnpersano.supertoasts.util.OnClickWrapper
import com.github.johnpersano.supertoasts.util.Wrappers
import com.skalski.websocketsclient.secureWebSocktes.WebSocket.WebSocketConnectionObserver
import com.skalski.websocketsclient.secureWebSocktes.WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification
import com.skalski.websocketsclient.secureWebSocktes.WebSocketConnection
import com.skalski.websocketsclient.secureWebSocktes.WebSocketException
import com.skalski.websocketsclient.secureWebSocktes.WebSocketOptions
import de.psdev.licensesdialog.LicensesDialog
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

class ActivityMain : Activity(), WebSocketConnectionObserver {
    @Volatile
    private var isConnected = false
    private var wsConnection: WebSocketConnection? = null
    private var wsURI: URI? = null
    private lateinit var cmdInput: EditText
    private lateinit var cmdOutput: TextView
    private lateinit var connectButton: CircularProgressButton
    private lateinit var hostname: EditText
    private lateinit var portNumber: EditText
    private lateinit var timeout: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wrappers = Wrappers()
        wrappers.add(onClickWrapperExit)
        SuperActivityToast.onRestoreState(savedInstanceState, this@ActivityMain, wrappers)
        hostname = findViewById(R.id.hostname)
        portNumber = findViewById(R.id.port)
        timeout = findViewById(R.id.timeout)
        cmdInput = findViewById(R.id.cmdInput)
        cmdOutput = findViewById(R.id.cmdOutput)
        connectButton = findViewById(R.id.btnConnect)
        cmdOutput.movementMethod = ScrollingMovementMethod()
        connectButton.isIndeterminateProgressMode = true

        connectButton.setOnClickListener(View.OnClickListener {
            if (connectButton.progress == 0) {
                connectButton.progress = 50
                if (hostname.text.toString() == "" ||
                    portNumber.text.toString() == "" ||
                    timeout.text.toString() == ""
                ) {
                    Timber.e("Invalid connection settings")
                    showInfo(resources.getString(R.string.info_msg_1), false)
                    connectButton.progress = -1
                    return@OnClickListener
                }

                /* save last settings */ActivitySettings.prefHostname(baseContext, hostname.text.toString())
                ActivitySettings.pref_set_port_number(baseContext, portNumber.text.toString())
                ActivitySettings.pref_set_timeout(baseContext, timeout.text.toString())

                /* connect */if (!wsConnect()) {
                    showInfo(resources.getString(R.string.info_msg_2), false)
                    connectButton.progress = -1
                }
            } else if (connectButton.progress == -1) {
                connectButton.progress = 0
            }
        })

        cmdInput.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                wsSend()
                return@OnEditorActionListener true
            }
            false
        })
    }

    public override fun onResume() {
        hostname.setText(ActivitySettings.pref_get_hostname(baseContext))
        portNumber.setText(ActivitySettings.pref_get_port_number(baseContext))
        timeout.setText(ActivitySettings.prefTimeout(baseContext))
        super.onResume()
    }

    private fun wsConnect(): Boolean {
        if (!isConnected) {
            wsConnection = WebSocketConnection()
            val wsOptions = WebSocketOptions()
            wsOptions.socketConnectTimeout = timeout.text.toString().toInt()
            try {
                wsURI = URI("ws://" + hostname.text.toString() + ":" + portNumber.text.toString())
                wsConnection!!.connect(wsURI, this, wsOptions)
            } catch (e: WebSocketException) {
                Timber.e("Can't connect to server - 'WebSocketException'")
                isConnected = false
                return false
            } catch (e1: URISyntaxException) {
                Timber.e("Can't connect to server - 'URISyntaxException'")
                isConnected = false
                return false
            } catch (ex: Exception) {
                Timber.e("Can't connect to server - 'Exception'")
                isConnected = false
                return false
            }
            Timber.i("Connected")
            isConnected = true
            return true
        }
        Timber.w("You are already connected to the server")
        return true
    }

    private fun wsDisconnect() {
        if (isConnected) {
            Timber.i("Disconnected")
            wsConnection!!.disconnect()
            connectButton.progress = 0
        }
    }

    private fun wsSend() {
        if (isConnected) {

            /* send message to the server */
            Timber.i("Message has been successfully sent")
            wsConnection!!.sendTextMessage(cmdInput.text.toString())
            appendText(cmdOutput, "[CLIENT] ${cmdInput.text}".trimIndent(), Color.RED)
        } else {
            /* no connection to the server */
            connectButton.progress = -1
            showInfo(resources.getString(R.string.info_msg_2), false)
        }
        cmdInput.text.clear()
    }

    override fun onOpen() {
        Timber.i("onOpen() - connection opened to: ${wsURI}")
        isConnected = true
        connectButton.progress = 100
    }

    override fun onClose(code: WebSocketCloseNotification, reason: String) {
        Timber.i("onClose() - ${code.name} $reason")
        isConnected = false
        connectButton.progress = 0
    }

    override fun onTextMessage(payload: String) {
        try {
            Timber.i(payload)
            val jsonObj = JSONObject(payload)
            if (jsonObj.has(TAG_JSON_TYPE) && jsonObj.has(TAG_JSON_MSG)) {

                if (jsonObj.getString(TAG_JSON_TYPE) == "notification") {
                    if (ActivitySettings.pref_notifications_disabled(baseContext)) {
                        Timber.i("Notifications are disabled")
                    } else {
                        val notificationId: Int =
                            if (ActivitySettings.pref_multiple_notifications_disabled(baseContext)) 0 else System.currentTimeMillis()
                                .toInt()

                        val newNotification = Notification.Builder(this)
                            .setContentTitle(resources.getString(R.string.app_name))
                            .setContentText(jsonObj.getString(TAG_JSON_MSG))
                            .setSmallIcon(R.drawable.ic_launcher).build()
                        newNotification.defaults = newNotification.defaults or Notification.DEFAULT_ALL
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(notificationId, newNotification)
                        appendText(cmdOutput, "[SERVER] Asynchronous Notification\n", Color.parseColor("#ff0099cc"))
                    }
                } else if (jsonObj.getString(TAG_JSON_TYPE) == "standard") {
                    appendText(cmdOutput, "[SERVER] ${jsonObj.getString(TAG_JSON_MSG)}".trimIndent(), Color.parseColor("#7DA30E"))
                } else {
                    showInfo(resources.getString(R.string.info_msg_4), false)
                    Timber.e("Received invalid JSON from server")
                }
            }
        } catch (e: JSONException) {
            /* String value */
            appendText(cmdOutput, "[SERVER] $payload".trimIndent(), Color.parseColor("#7DA30E"))
        }
    }

    override fun onRawTextMessage(payload: ByteArray) {
        Timber.e("We didn't expect 'RawTextMessage'")
    }

    override fun onBinaryMessage(payload: ByteArray) {
        Timber.e("We didn't expect 'BinaryMessage'")
    }

    fun showInfo(info: String?, showButton: Boolean) {
        val superActivityToast: SuperActivityToast
        if (showButton) {
            superActivityToast = SuperActivityToast(this@ActivityMain, SuperToast.Type.BUTTON)
            superActivityToast.setOnClickWrapper(onClickWrapperExit)
            superActivityToast.setButtonIcon(SuperToast.Icon.Dark.EXIT, "Exit")
        } else {
            superActivityToast = SuperActivityToast(this@ActivityMain, SuperToast.Type.STANDARD)
            superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT)
        }
        superActivityToast.duration = SuperToast.Duration.EXTRA_LONG
        superActivityToast.animations = SuperToast.Animations.FLYIN
        superActivityToast.background = SuperToast.Background.RED
        superActivityToast.text = info
        superActivityToast.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            Timber.i("Starting 'ActivitySettings'")
            val intent = Intent(applicationContext, ActivitySettings::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.action_licenses) {
            Timber.i("Starting 'LicensesDialog'")
            LicensesDialog(this@ActivityMain, R.raw.notices, false, true).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            onBackPressed()
        }
        return true
    }

    private var onClickWrapperExit = OnClickWrapper("id_exit") { _, _ ->
        wsDisconnect()
        finish()
    }

    override fun onBackPressed() {
        showInfo(resources.getString(R.string.info_msg_3), true)
    }

    companion object {
        private const val TAG_JSON_TYPE = "Type"
        private const val TAG_JSON_MSG = "Message"

        fun appendText(textView: TextView, text: String, textColor: Int) {
            val start: Int = textView.text.length
            textView.append("\n$text")
            val end: Int = textView.text.length
            val spannableText = textView.text as Spannable
            spannableText.setSpan(ForegroundColorSpan(textColor), start, end, 0)
        }
    }
}
