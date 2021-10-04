package com.skalski.websocketsclient.SecureWebSocktes;

import java.net.URI;

public interface WebSocket {

    /**
     * Session handler for WebSocket sessions.
     */
    interface WebSocketConnectionObserver {
        enum WebSocketCloseNotification {
            CANNOT_CONNECT,
            CONNECTION_LOST,
            PROTOCOL_ERROR,
            INTERNAL_ERROR,
            SERVER_ERROR,
            RECONNECT
        }

        /**
         * Fired when the WebSockets connection has been established.
         * After this happened, messages may be sent.
         */
        void onOpen();

        /**
         * Fired when the WebSockets connection has deceased (or could
         * not established in the first place).
         *
         * @param code   Close code.
         * @param reason Close reason (human-readable).
         */
        void onClose(WebSocketCloseNotification code, String reason);

        /**
         * Fired when a text message has been received (and text
         * messages are not set to be received raw).
         *
         * @param payload Text message payload or null (empty payload).
         */
        void onTextMessage(String payload);

        /**
         * Fired when a text message has been received (and text
         * messages are set to be received raw).
         *
         * @param payload Text message payload as raw UTF-8 or null (empty payload).
         */
        void onRawTextMessage(byte[] payload);

        /**
         * Fired when a binary message has been received.
         *
         * @param payload Binar message payload or null (empty payload).
         */
        void onBinaryMessage(byte[] payload);
    }

    void connect(URI uri, WebSocketConnectionObserver observer) throws WebSocketException;

    void connect(URI uri, WebSocketConnectionObserver observer, WebSocketOptions options) throws WebSocketException;

    void disconnect();

    boolean isConnected();

    void sendBinaryMessage(byte[] payload);

    void sendRawTextMessage(byte[] payload);

    void sendTextMessage(String payload);
}
