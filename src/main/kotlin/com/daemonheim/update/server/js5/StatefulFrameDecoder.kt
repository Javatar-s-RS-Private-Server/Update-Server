package com.daemonheim.update.server.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.util.*

abstract class StatefulFrameDecoder<T : Enum<T>>(private var state: T) : ByteToMessageDecoder() {

    /**
     * Sets a new state.
     *
     * @param state The new state.
     * @throws NullPointerException If the state is `null`.
     */
    fun setState(state: T) {
        this.state = Objects.requireNonNull(state, "State cannot be null.")
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        decode(ctx, buf, out, state)
    }

    /**
     * Decodes the received packets into a frame.
     *
     * @param ctx The current context of this handler.
     * @param in The cumulative buffer, which may contain zero or more bytes.
     * @param out The [List] of objects to pass forward through the pipeline.
     * @param state The current state. The state may be changed by calling [.setState].
     * @throws Exception If there is an exception when decoding a frame.
     */
    @Throws(Exception::class)
    protected abstract fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: T)

}