package com.daemonheim.update.server

import com.daemonheim.update.server.channel.ChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlin.concurrent.thread

class RuneScapeFileServerEngine(val environment: RuneScapeFileServerEnvironment) {

    val config = environment.config

    private val bootstrap = ServerBootstrap()
    private val bossGroup = NioEventLoopGroup(2)
    private val workerGroup = NioEventLoopGroup(1)

    fun start() {

        Runtime.getRuntime().addShutdownHook(thread(false) {
            stop()
            environment.cache.close()
        })

        println("Starting File Server on port ${config.port}...")

        bootstrap.group(bossGroup, workerGroup)
            .childAttr(RuneScapeFileServerSession.SESSION_KEY, RuneScapeFileServerSession(environment.cache, config.revision))
            .channel(NioServerSocketChannel::class.java)
            .childHandler(ChannelInitializer())
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .bind(config.port)
    }

    fun stop() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

}