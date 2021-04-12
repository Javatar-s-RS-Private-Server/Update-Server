package com.daemonheim.update.server

import com.daemonheim.update.server.JS5Session.Companion.session
import com.daemonheim.update.server.js5.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.runelite.cache.fs.Store

class JS5ChannelHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is LoginResultType) {
            if (msg == LoginResultType.REVISION_MISMATCH) {
                ctx.writeAndFlush(msg)
                ctx.close()
                return
            } else if(msg == LoginResultType.ACCEPTABLE) {
                ctx.writeAndFlush(msg)
                ctx.pipeline().replace("decoder", "decoder", FilestoreDecoder(194))
                ctx.pipeline().replace("encoder", "encoder", FilestoreEncoder())
            }
        } else if(msg is FilestoreRequest) {
            val system = FilestoreSystem(ctx.session)
            system.receiveMessage(ctx, msg)
        }
    }
}