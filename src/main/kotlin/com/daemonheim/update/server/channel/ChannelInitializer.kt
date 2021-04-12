package com.daemonheim.update.server.channel

import com.daemonheim.update.server.codec.HandshakeCodec
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class ChannelInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast("codec", HandshakeCodec())
        ch.pipeline().addLast("handler", ChannelHandler())
    }
}