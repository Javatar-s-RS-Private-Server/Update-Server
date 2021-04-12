package com.daemonheim.update.server

import com.daemonheim.update.server.JS5Session.Companion.SESSION_KEY
import com.displee.cache.CacheLibrary
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

object Server {

    @JvmStatic
    fun main(args: Array<String>) {
        start()
    }

    fun start() {
        val bootstrap = ServerBootstrap()
        val bossGroup = NioEventLoopGroup(2)
        val workerGroup = NioEventLoopGroup(1)
        bootstrap.group(bossGroup, workerGroup)
            .childAttr(SESSION_KEY, JS5Session(CacheLibrary.create("/home/javatar/Downloads/latest/cache")))
            .channel(NioServerSocketChannel::class.java)
            .childHandler(JS5ChannelInitializer())
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .bind(8080)
    }

}