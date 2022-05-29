package com.skalski.websocketsclient.secureWebSocktes

class WebSocketFrameHeader {
    var opcode = 0
    var isFin = false
    var reserved = 0
    var headerLength = 0
    var payloadLength = 0
    var totalLength = 0
        private set
    var mask: ByteArray? = null
    fun setTotalLen(totalLength: Int) {
        this.totalLength = totalLength
    }
}