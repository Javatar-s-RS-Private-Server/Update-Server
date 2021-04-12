package com.daemonheim.update.server

import com.daemonheim.update.server.JS5Session.Companion.session
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class JS5Codec : ByteToMessageCodec<FileRequest>() {
    override fun encode(ctx: ChannelHandlerContext, msg: FileRequest, out: ByteBuf) {

        val data = msg.respondFile()

        if (data.isNotEmpty()) {
            out.writeByte(msg.indexId)
            out.writeShort(msg.archiveId)
            data.forEach {
                if(out.writerIndex() % 512 == 0) {
                    out.writeByte(-1)
                }
                out.writeByte(it.toInt())
            }
        }

    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val buffer = ctx.alloc().buffer(buf.readableBytes())
        buf.duplicate().readBytes(buffer)
        buf.skipBytes(buf.readableBytes())
        val session = ctx.session
        val opcode = buffer.readUnsignedByte().toInt()
        println("Handling JS5 opcode $opcode")
        if(opcode in 0..4) {
            val request = session.getFile(opcode, buffer)
            if(request != null)
                out.add(request)
        } else {
            println("Unhandled opcode $opcode")
            ctx.disconnect()
        }
    }
}