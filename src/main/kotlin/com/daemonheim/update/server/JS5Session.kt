package com.daemonheim.update.server

import com.daemonheim.update.server.impl.*
import com.daemonheim.update.server.js5.FilestoreSystem
import com.displee.cache.CacheLibrary
import io.netty.buffer.ByteBuf
import io.netty.util.AttributeKey
import io.netty.util.AttributeMap
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.jagex.DiskStorage
import java.io.File

class JS5Session(val cache: CacheLibrary) {

    var js5Key: Int = 0

    val store = Store(DiskStorage(File(cache.path))).also {
        it.load()
    }

    fun getFile(opcode: Int, buffer: ByteBuf) : FileRequest? {
        return when(opcode) {
            0 -> JS5PrefetchFileRequest(buffer.readUnsignedByte().toInt(), buffer.readUnsignedShort(), this)
            1 -> JS5UrgentFileRequest(buffer.readUnsignedByte().toInt(), buffer.readUnsignedShort(), this)
            2 -> {
                buffer.skipBytes(3)
                byteArrayOf()
                JS5FinishFileRequest(-1, -1)
            }
            3 -> {
                buffer.skipBytes(3)
                byteArrayOf()
                JS5PrepareFileRequest(-1, -1)
            }
            4 -> {
                js5Key = buffer.readByte().toInt()
                buffer.skipBytes(2)
                byteArrayOf()
                JS5XorFileRequest(-1, -1)
            }
            else -> null
        }
    }

    companion object {
        val SESSION_KEY: AttributeKey<JS5Session> = AttributeKey.valueOf("runescape_session")

        val AttributeMap.session
            get() = attr(SESSION_KEY).get()
    }
}