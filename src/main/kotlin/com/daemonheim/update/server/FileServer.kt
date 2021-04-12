package com.daemonheim.update.server

object FileServer {

    fun start(configure: RuneScapeFileServerConfiguration.() -> Unit) {
        val configuration = RuneScapeFileServerConfiguration().apply(configure)
        val environment = RuneScapeFileServerEnvironment(configuration)
        val engine = RuneScapeFileServerEngine(environment)
        environment.start()
        engine.start()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        start {
            port = 8080
            revision = 194
            cachePath = "/home/javatar/Downloads/latest/cache"
        }
    }

}