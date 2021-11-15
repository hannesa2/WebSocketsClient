package com.skalski.websocketsclient.secureWebSocktes

import kotlin.Throws
import java.net.URI

interface WebSocket {
    /**
     * Session handler for WebSocket sessions.
     */
    interface WebSocketConnectionObserver {
        enum class WebSocketCloseNotification {
            CANNOT_CONNECT, CONNECTION_LOST, PROTOCOL_ERROR, INTERNAL_ERROR, SERVER_ERROR, RECONNECT
        }

        /**
         * Fired when the WebSockets connection has been established.
         * After this happened, messages may be sent.
         */
        fun onOpen()

        /**
         * Fired when the WebSockets connection has deceased (or could
         * not established in the first place).
         *
         * @param code   Close code.
         * @param reason Close reason (human-readable).
         */
        fun onClose(code: WebSocketCloseNotification, reason: String)

        /**
         * Fired when a text message has been received (and text
         * messages are not set to be received raw).
         *
         * @param payload Text message payload or null (empty payload).
         */
        fun onTextMessage(payload: String)

        /**
         * Fired when a text message has been received (and text
         * messages are set to be received raw).
         *
         * @param payload Text message payload as raw UTF-8 or null (empty payload).
         */
        fun onRawTextMessage(payload: ByteArray)

        /**
         * Fired when a binary message has been received.
         *
         * @param payload Binar message payload or null (empty payload).
         */
        fun onBinaryMessage(payload: ByteArray)
    }

    @Throws(WebSocketException::class)
    fun connect(uri: URI, observer: WebSocketConnectionObserver)

    @Throws(WebSocketException::class)
    fun connect(uri: URI?, observer: WebSocketConnectionObserver, options: WebSocketOptions)
    fun disconnect()
    val isConnected: Boolean
    fun sendBinaryMessage(payload: ByteArray)
    fun sendRawTextMessage(payload: ByteArray)
    fun sendTextMessage(payload: String)
}