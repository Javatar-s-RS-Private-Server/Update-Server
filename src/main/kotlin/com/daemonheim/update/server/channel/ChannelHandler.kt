package com.daemonheim.update.server.channel

import com.daemonheim.update.server.RuneScapeFileServerSession.Companion.session
import com.daemonheim.update.server.codec.FilestoreCodec
import com.daemonheim.update.server.fs.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ChannelHandler : ChannelInboundHandlerAdapter() {
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
        } else if(msg is FileRequest) {
            ctx.session.receiveMessage(ctx, msg)
        }
    }
}