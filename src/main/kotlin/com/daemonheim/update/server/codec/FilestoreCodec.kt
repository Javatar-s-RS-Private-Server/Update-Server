package com.daemonheim.update.server.codec

import com.daemonheim.update.server.js5.FilestoreRequest
import com.daemonheim.update.server.js5.FilestoreResponse
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class FilestoreCodec : ByteToMessageCodec<FilestoreResponse>() {

    override fun encode(ctx: ChannelHandlerContext, msg: FilestoreResponse, out: ByteBuf) {
        out.writeByte(msg.index)
        out.writeShort(msg.archive)

        msg.data.forEach { data ->
            if (out.writerIndex() % 512 == 0) {
                out.writeByte(-1)
            }
            out.writeByte(data.toInt())
        }
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (!buf.isReadable) {
            return
        }
        buf.markReaderIndex()
        val opcode = buf.readByte().toInt()
        println("Archive Request $opcode")
        when (opcode) {
            CLIENT_INIT_GAME, CLIENT_LOAD_SCREEN, CLIENT_INIT_OPCODE -> {
                buf.skipBytes(3)
            }
            ARCHIVE_REQUEST_NEUTRAL, ARCHIVE_REQUEST_URGENT -> {
                if (buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()

                    val request = FilestoreRequest(index = index, archive = archive, priority = opcode == ARCHIVE_REQUEST_URGENT)
                    out.add(request)
                } else {
                    buf.resetReaderIndex()
                }
            }
            else -> {
                error("Unhandled opcode: $opcode")
            }
        }
    }

    companion object {
        private const val ARCHIVE_REQUEST_URGENT = 0
        private const val ARCHIVE_REQUEST_NEUTRAL = 1
        private const val CLIENT_INIT_GAME = 2
        private const val CLIENT_LOAD_SCREEN = 3
        private const val CLIENT_INIT_OPCODE = 6
    }
}
