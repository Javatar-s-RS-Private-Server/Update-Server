package com.daemonheim.update.server.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext

/**
 * @author Tom <rspsmods@gmail.com>
 */
class FilestoreDecoder(private val serverRevision: Int) : StatefulFrameDecoder<FilestoreDecoderState>(
    FilestoreDecoderState.REVISION_REQUEST
) {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: FilestoreDecoderState) {
        decodeArchiveRequest(buf, out)
    }

    private fun decodeRevisionRequest(ctx: ChannelHandlerContext, buf: ByteBuf) {
        println("Handshake ${buf.readableBytes()}")
        if (buf.readableBytes() >= 4) {
            val revision = buf.readInt()
            if (revision != serverRevision) {
                ctx.writeAndFlush(LoginResultType.REVISION_MISMATCH).addListener(ChannelFutureListener.CLOSE)
            } else {
                println(revision)
                setState(FilestoreDecoderState.ARCHIVE_REQUEST)
                ctx.writeAndFlush(LoginResultType.ACCEPTABLE)
            }
        }
    }

    private fun decodeArchiveRequest(buf: ByteBuf, out: MutableList<Any>) {
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
