package com.daemonheim.update.server

import com.daemonheim.update.server.JS5Session.Companion.session
import com.daemonheim.update.server.codec.FilestoreCodec
import com.daemonheim.update.server.js5.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class JS5ChannelHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is HandshakeResult) {
            if (msg == HandshakeResult.REVISION_MISMATCH) {
                ctx.writeAndFlush(msg)
                ctx.close()
                return
            } else if(msg == HandshakeResult.ACCEPTABLE) {
                ctx.writeAndFlush(msg)
                ctx.pipeline().replace("codec", "codec", FilestoreCodec())
            }
        } else if(msg is FilestoreRequest) {
            ctx.session.receiveMessage(ctx, msg)
        }
    }
}