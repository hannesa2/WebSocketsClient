package com.skalski.websocketsclient.secureWebSocktes

/**
 * WebSockets connection options. This can be supplied to WebSocketConnection in connect().
 * Note that the latter copies the options provided to connect(), so any change after
 * connect will have no effect.
 */
class WebSocketOptions {

    var receiveTextMessagesRaw: Boolean

    /**
     * Set TCP No-Delay ("Nagle") for TCP connection.
     *
     * DEFAULT: true
     */
    private var tcpNoDelay: Boolean

    /**
     * Get UTF-8 validation option.
     *
     * @return           True, iff incoming UTF-8 is validated.
     */
    var validateIncomingUtf8: Boolean

    /**
     * Get mask client frames option.
     *
     * @return        True, iff client-to-server frames are masked.
     */
    var maskClientFrames: Boolean

    /**
     * Set reconnect interval
     *
     * @param reconnectInterval    Interval in ms, 0 - no reconnection
     */
    var reconnectInterval: Int

    constructor() {
        maxFramePayloadSize = 128 * 1024
        maxMessagePayloadSize = 128 * 1024
        receiveTextMessagesRaw = false
        tcpNoDelay = true
        socketReceiveTimeout = 200
        socketConnectTimeout = 6000
        validateIncomingUtf8 = true
        maskClientFrames = true
        reconnectInterval = 0 // no reconnection by default
    }

    constructor(other: WebSocketOptions) {
        maxFramePayloadSize = other.maxFramePayloadSize
        maxMessagePayloadSize = other.maxMessagePayloadSize
        receiveTextMessagesRaw = other.receiveTextMessagesRaw
        tcpNoDelay = other.tcpNoDelay
        socketReceiveTimeout = other.socketReceiveTimeout
        socketConnectTimeout = other.socketConnectTimeout
        validateIncomingUtf8 = other.validateIncomingUtf8
        maskClientFrames = other.maskClientFrames
        reconnectInterval = other.reconnectInterval
    }

    /**
     * Get maximum frame payload size that will be accepted
     * when receiving.
     *
     * @return           Maximum size in octets for frame payload.
     */
    var maxFramePayloadSize: Int
        set(size) {
            if (size > 0) {
                field = size
                if (maxMessagePayloadSize < field) {
                    maxMessagePayloadSize = field
                }
            }
        }
    /**
     * Get maximum message payload size (after reassembly of fragmented
     * messages) that will be accepted when receiving.
     *
     * @return           Maximum size in octets for message payload.
     */
    var maxMessagePayloadSize: Int
        set(size) {
            if (size > 0) {
                field = size
                if (field < maxFramePayloadSize) {
                    maxFramePayloadSize = field
                }
            }
        }

    /**
     * Get socket receive timeout.
     *
     * @return           Socket receive timeout in ms.
     */
    var socketReceiveTimeout: Int
        set(timeoutMs) {
            if (timeoutMs >= 0) {
                field = timeoutMs
            }
        }

    /**
     * Get socket connect timeout.
     *
     * @return           Socket receive timeout in ms.
     */
    var socketConnectTimeout: Int
        set(timeoutMs) {
            if (timeoutMs >= 0) {
                field = timeoutMs
            }
        }
}
