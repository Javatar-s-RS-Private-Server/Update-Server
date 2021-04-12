package com.daemonheim.update.server

interface FileRequest {

    val indexId: Int
    val archiveId: Int

    fun prepareFile()
    fun respondFile() : ByteArray

}