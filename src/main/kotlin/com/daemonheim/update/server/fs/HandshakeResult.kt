package com.daemonheim.update.server.fs

enum class HandshakeResult(val id: Int) {
    ACCEPTABLE(0),
    REVISION_MISMATCH(6)
}