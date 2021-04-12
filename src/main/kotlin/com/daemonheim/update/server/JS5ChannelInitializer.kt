package com.daemonheim.update.server

import com.daemonheim.update.server.js5.*
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class JS5ChannelInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast("encoder", HandshakeEncoder())
        ch.pipeline().addLast("decoder", HandshakeDecoder(194))
        ch.pipeline().addLast("handler", JS5ChannelHandler())
    }
}