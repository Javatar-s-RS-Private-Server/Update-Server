package com.daemonheim.update.server.codec

import com.daemonheim.update.server.fs.HandshakeResult
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class HandshakeCodec : ByteToMessageCodec<HandshakeResult>() {
    override fun encode(ctx: ChannelHandlerContext, msg: HandshakeResult, out: ByteBuf) {
        out.writeByte(msg.id)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (!buf.isReadable) {
            return
        }
        when (buf.readByte().toInt()) {
            15 -> {
                val revision = buf.readInt()
                if(revision != 194) {
                    out.add(HandshakeResult.REVISION_MISMATCH)
                } else {
                    out.add(HandshakeResult.ACCEPTABLE)
                }
            }
            else -> {
                buf.readBytes(buf.readableBytes())
                return
            }
        }
    }
}