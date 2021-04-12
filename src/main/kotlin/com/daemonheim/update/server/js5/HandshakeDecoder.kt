package com.daemonheim.update.server.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * A [ByteToMessageDecoder] implementation which is responsible for handling
 * the initial handshake signal from the client. The implementation is dependant
 * on the network module.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class HandshakeDecoder(private val revision: Int) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (!buf.isReadable) {
            return
        }

        val opcode = buf.readByte().toInt()
        when (opcode) {
            15 -> {
                val revision = buf.readInt()
                if(revision != 194) {
                    out.add(LoginResultType.REVISION_MISMATCH)
                } else {
                    out.add(LoginResultType.ACCEPTABLE)
                }
            }
            else -> {
                /*
                 * If the handshake type is not handled, we want to log it and
                 * make sure we read any bytes from the buffer.
                 */
                buf.readBytes(buf.readableBytes())
                return
            }
        }
        /*
         * This decoder is no longer needed for this context, so we discard it.
         */
        ctx.writeAndFlush(HandshakeDecoder(opcode))
    }
}
