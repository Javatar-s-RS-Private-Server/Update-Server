package com.daemonheim.update.server

import java.net.InetSocketAddress
import java.net.Socket

object Client {

    @JvmStatic
    fun main(args: Array<String>) {

        val socket = Socket()
        socket.connect(InetSocketAddress(8080))
        socket.getOutputStream().write(15)
    }

}