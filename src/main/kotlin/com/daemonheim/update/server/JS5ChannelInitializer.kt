package com.daemonheim.update.server

import com.daemonheim.update.server.codec.HandshakeCodec
import com.daemonheim.update.server.js5.*
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class JS5ChannelInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast("codec", HandshakeCodec())
        ch.pipeline().addLast("handler", JS5ChannelHandler())
    }
}