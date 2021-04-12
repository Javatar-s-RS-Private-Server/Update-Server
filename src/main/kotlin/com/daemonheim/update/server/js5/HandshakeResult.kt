package com.daemonheim.update.server.js5

enum class HandshakeResult(val id: Int) {
    ACCEPTABLE(0),
    REVISION_MISMATCH(6)
}