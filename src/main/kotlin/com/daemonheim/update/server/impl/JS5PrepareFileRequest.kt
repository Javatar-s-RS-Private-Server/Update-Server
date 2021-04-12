package com.daemonheim.update.server.impl

import com.daemonheim.update.server.FileRequest

class JS5PrepareFileRequest(override val indexId: Int, override val archiveId: Int) : FileRequest {
    override fun prepareFile() {
    }

    override fun respondFile(): ByteArray {
        return byteArrayOf()
    }
}