package com.daemonheim.update.server.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class FileStoreHandshakeEncoder : MessageToByteEncoder<LoginResultType>() {
    override fun encode(ctx: ChannelHandlerContext, msg: LoginResultType, out: ByteBuf) {
        out.writeByte(msg.id)
    }
}