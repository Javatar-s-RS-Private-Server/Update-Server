package com.daemonheim.update.server

import com.displee.cache.CacheLibrary

class RuneScapeFileServerEnvironment(val config : RuneScapeFileServerConfiguration) {

    val cache: CacheLibrary by lazy { CacheLibrary.create(config.cachePath) }

    fun start() {

    }

    fun stop() {

    }

}