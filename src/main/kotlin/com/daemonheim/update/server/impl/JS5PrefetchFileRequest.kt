package com.daemonheim.update.server.impl

import com.daemonheim.update.server.FileRequest
import com.daemonheim.update.server.JS5Session

class JS5PrefetchFileRequest(override val indexId: Int, override val archiveId: Int, val session: JS5Session) :
    FileRequest {
    override fun prepareFile() {
    }

    override fun respondFile(): ByteArray {
        return JS5UrgentFileRequest(indexId, archiveId, session).respondFile()
    }
}