package com.daemonheim.update.server.impl

import com.daemonheim.update.server.FileRequest
import com.daemonheim.update.server.JS5Session
import com.displee.compress.CompressionType
import com.displee.compress.compress
import com.displee.io.impl.OutputBuffer

class JS5UrgentFileRequest(override val indexId: Int, override val archiveId: Int, val session: JS5Session) :
    FileRequest {

    override fun prepareFile() {

    }

    override fun respondFile(): ByteArray {
        val cache = session.cache
        println("Index $indexId - $archiveId")
        val data = if (indexId == 255 && archiveId == 255) {
            cache.generateOldUkeys().compress(CompressionType.NONE)
        } else if (indexId == 255) {
            cache.index255?.readArchiveSector(archiveId)?.data
        } else {
            var data = cache.index(indexId).readArchiveSector(archiveId)?.data
            if (data != null) {
                val compression = data[0].toInt() and 255
                val compressedSize = getInt(data[1], data[2], data[3], data[4])
                val expectedSize: Int = (1 + 4 + compressedSize + if (compression != CompressionType.NONE.ordinal) 4 else 0)
                if (data.size != expectedSize && (data.size - expectedSize) == 2) {
                    data = data.copyOf(data.size - 2)
                }
            }
            data
        }
        if (data == null) {
            return byteArrayOf()
        }
        return data
    }

    private fun getInt(b1: Byte, b2: Byte, b3: Byte, b4: Byte) =
        b1.toInt() shl 24 or (b2.toInt() and 0xff shl 16) or (b3.toInt() and 0xff shl 8) or (b4.toInt() and 0xff)
}