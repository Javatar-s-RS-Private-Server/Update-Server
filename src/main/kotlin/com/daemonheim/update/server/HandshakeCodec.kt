package com.daemonheim.update.server

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class HandshakeCodec : ByteToMessageCodec<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val buffer = ctx.alloc().buffer(buf.readableBytes())
        buf.duplicate().readBytes(buffer)
        buf.skipBytes(buf.readableBytes())
        val opcode = buffer.readUnsignedByte().toInt()
        println("Handling Handshake opcode $opcode")
        if(opcode == 15) {
            val revision = buffer.readInt()
            if(revision != 194) {
                out.add(Unpooled.wrappedBuffer(byteArrayOf(6)))
            } else {
                out.add(Unpooled.wrappedBuffer(byteArrayOf(0)))
                out.add(CodecUpgrade())
            }
        } else {
            ctx.close()
        }
    }
}