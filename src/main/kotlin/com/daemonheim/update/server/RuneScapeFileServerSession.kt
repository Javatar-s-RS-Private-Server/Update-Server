package com.daemonheim.update.server

import com.daemonheim.update.server.fs.FileRequest
import com.daemonheim.update.server.fs.FileSystem
import com.displee.cache.CacheLibrary
import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import io.netty.util.AttributeMap

class RuneScapeFileServerSession(val cache: CacheLibrary, val revision: Int) {

    private val system = FileSystem(this)

    fun receiveMessage(ctx: ChannelHandlerContext, msg: FileRequest) {
        system.receiveMessage(ctx, msg)
    }

    companion object {
        val SESSION_KEY: AttributeKey<RuneScapeFileServerSession> = AttributeKey.valueOf("runescape_session")

        val AttributeMap.session
            get() = attr(SESSION_KEY).get()
    }
}