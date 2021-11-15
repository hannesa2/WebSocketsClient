package com.skalski.websocketsclient.secureWebSocktes

/**
 * WebSockets connection options. This can be supplied to WebSocketConnection in connect().
 * Note that the latter copies the options provided to connect(), so any change after
 * connect will have no effect.
 */
class WebSocketOptions {
    private var mMaxFramePayloadSize: Int
    private var mMaxMessagePayloadSize: Int
    /**
     * When true, WebSockets text messages are provided as
     * verified, but non-decoded UTF-8 in byte arrays.
     *
     * @return           True, iff option is enabled.
     */
    /**
     * Receive text message as raw byte array with verified,
     * but non-decoded UTF-8.
     *
     * DEFAULT: false
     *
     * @param enabled    True to enable.
     */
    var receiveTextMessagesRaw: Boolean
    /**
     * Get TCP No-Delay ("Nagle") for TCP connection.
     *
     * @return           True, iff TCP No-Delay is enabled.
     */
    /**
     * Set TCP No-Delay ("Nagle") for TCP connection.
     *
     * DEFAULT: true
     *
     * @param enabled    True to enable TCP No-Delay.
     */
    var tcpNoDelay: Boolean
    private var mSocketReceiveTimeout: Int
    private var mSocketConnectTimeout: Int
    /**
     * Get UTF-8 validation option.
     *
     * @return           True, iff incoming UTF-8 is validated.
     */
    /**
     * Controls whether incoming text message payload is verified
     * to be valid UTF-8.
     *
     * DEFAULT: true
     *
     * @param enabled    True to verify incoming UTF-8.
     */
    var validateIncomingUtf8: Boolean
    /**
     * Get mask client frames option.
     *
     * @return        True, iff client-to-server frames are masked.
     */
    /**
     * Controls whether to mask client-to-server WebSocket frames.
     * Beware, normally, WebSockets servers will deny non-masked c2s
     * frames and fail the connection.
     *
     * DEFAULT: true
     *
     * @param enabled   Set true to mask client-to-server frames.
     */
    var maskClientFrames: Boolean

    /**
     * Set reconnect interval
     *
     * @param reconnectInterval    Interval in ms, 0 - no reconnection
     */
    var reconnectInterval: Int

    /**
     * Construct default options.
     */
    constructor() {
        mMaxFramePayloadSize = 128 * 1024
        mMaxMessagePayloadSize = 128 * 1024
        receiveTextMessagesRaw = false
        tcpNoDelay = true
        mSocketReceiveTimeout = 200
        mSocketConnectTimeout = 6000
        validateIncomingUtf8 = true
        maskClientFrames = true
        reconnectInterval = 0 // no reconnection by default
    }

    /**
     * Construct options as copy from other options object.
     *
     * @param other      Options to copy.
     */
    constructor(other: WebSocketOptions) {
        mMaxFramePayloadSize = other.mMaxFramePayloadSize
        mMaxMessagePayloadSize = other.mMaxMessagePayloadSize
        receiveTextMessagesRaw = other.receiveTextMessagesRaw
        tcpNoDelay = other.tcpNoDelay
        mSocketReceiveTimeout = other.mSocketReceiveTimeout
        mSocketConnectTimeout = other.mSocketConnectTimeout
        validateIncomingUtf8 = other.validateIncomingUtf8
        maskClientFrames = other.maskClientFrames
        reconnectInterval = other.reconnectInterval
    }
    /**
     * Get maxium frame payload size that will be accepted
     * when receiving.
     *
     * @return           Maximum size in octets for frame payload.
     */
    /**
     * Set maximum frame payload size that will be accepted
     * when receiving.
     *
     * DEFAULT: 4MB
     *
     * @param size       Maximum size in octets for frame payload.
     */
    var maxFramePayloadSize: Int
        get() = mMaxFramePayloadSize
        set(size) {
            if (size > 0) {
                mMaxFramePayloadSize = size
                if (mMaxMessagePayloadSize < mMaxFramePayloadSize) {
                    mMaxMessagePayloadSize = mMaxFramePayloadSize
                }
            }
        }
    /**
     * Get maximum message payload size (after reassembly of fragmented
     * messages) that will be accepted when receiving.
     *
     * @return           Maximum size in octets for message payload.
     */
    /**
     * Set maximum message payload size (after reassembly of fragmented
     * messages) that will be accepted when receiving.
     *
     * DEFAULT: 4MB
     *
     * @param size       Maximum size in octets for message payload.
     */
    var maxMessagePayloadSize: Int
        get() = mMaxMessagePayloadSize
        set(size) {
            if (size > 0) {
                mMaxMessagePayloadSize = size
                if (mMaxMessagePayloadSize < mMaxFramePayloadSize) {
                    mMaxFramePayloadSize = mMaxMessagePayloadSize
                }
            }
        }
    /**
     * Get socket receive timeout.
     *
     * @return           Socket receive timeout in ms.
     */
    /**
     * Set receive timeout on socket. When the TCP connection disappears,
     * that will only be recognized by the reader after this timeout.
     *
     * DEFAULT: 200
     *
     * @param timeoutMs  Socket receive timeout in ms.
     */
    var socketReceiveTimeout: Int
        get() = mSocketReceiveTimeout
        set(timeoutMs) {
            if (timeoutMs >= 0) {
                mSocketReceiveTimeout = timeoutMs
            }
        }
    /**
     * Get socket connect timeout.
     *
     * @return           Socket receive timeout in ms.
     */
    /**
     * Set connect timeout on socket. When a WebSocket connection is
     * about to be established, the TCP socket connect will timeout
     * after this period.
     *
     * DEFAULT: 3000
     *
     * @param timeoutMs  Socket connect timeout in ms.
     */
    var socketConnectTimeout: Int
        get() = mSocketConnectTimeout
        set(timeoutMs) {
            if (timeoutMs >= 0) {
                mSocketConnectTimeout = timeoutMs
            }
        }
}