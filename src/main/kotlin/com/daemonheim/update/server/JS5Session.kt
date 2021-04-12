package com.daemonheim.update.server

import com.daemonheim.update.server.js5.FilestoreRequest
import com.daemonheim.update.server.js5.FilestoreSystem
import com.displee.cache.CacheLibrary
import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import io.netty.util.AttributeMap

class JS5Session(val cache: CacheLibrary) {

    private val system = FilestoreSystem(this)

    fun receiveMessage(ctx: ChannelHandlerContext, msg: FilestoreRequest) {
        system.receiveMessage(ctx, msg)
    }

    companion object {
        val SESSION_KEY: AttributeKey<JS5Session> = AttributeKey.valueOf("runescape_session")

        val AttributeMap.session
            get() = attr(SESSION_KEY).get()
    }
}