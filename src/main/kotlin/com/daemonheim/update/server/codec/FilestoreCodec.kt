package com.daemonheim.update.server.codec

import com.daemonheim.update.server.fs.FileRequest
import com.daemonheim.update.server.fs.FileResponse
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class FilestoreCodec : ByteToMessageCodec<FileResponse>() {

    override fun encode(ctx: ChannelHandlerContext, msg: FileResponse, out: ByteBuf) {
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
        when (opcode) {
            CLIENT_INIT_GAME, CLIENT_LOAD_SCREEN, CLIENT_INIT_OPCODE -> {
                buf.skipBytes(3)
            }
            ARCHIVE_REQUEST_NEUTRAL, ARCHIVE_REQUEST_URGENT -> {
                if (buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()

                    val request = FileRequest(index = index, archive = archive, priority = opcode == ARCHIVE_REQUEST_URGENT)
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
