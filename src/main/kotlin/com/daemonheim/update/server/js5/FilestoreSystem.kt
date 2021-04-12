package com.daemonheim.update.server.js5

import com.daemonheim.update.server.JS5Session
import com.displee.compress.CompressionType
import io.netty.channel.ChannelHandlerContext
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.jagex.DiskStorage

/**
 * A [ServerSystem] responsible for sending decoded [filestore] data to the
 * [channel].
 *
 * @author Tom <rspsmods@gmail.com>
 */
class FilestoreSystem(private val session: JS5Session) {

    private val filestore: Store by lazy { session.store }

    /**
     * TODO(Tom): the logic for encoding the data should be handled
     * by a pipeline defined in the [net] module instead. This [ServerSystem]
     * should only be responsible for informing the pipeline that a request
     * was sent by the client.
     */

    fun receiveMessage(ctx: ChannelHandlerContext, msg: FilestoreRequest) {
        if (msg.index == 255) {
            encodeIndexData(ctx, msg)
        } else {
            encodeFileData(ctx, msg)
        }
    }

    fun terminate() {
    }

    private fun encodeIndexData(ctx: ChannelHandlerContext, req: FilestoreRequest) {
        val data: ByteArray

        if (req.archive == 255) {
            if (cachedIndexData == null) {
                val buf = ctx.alloc().heapBuffer(filestore.indexes.size * 8)

                filestore.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val container = Container(CompressionType.NONE.ordinal, -1)
                container.compress(buf.array().copyOf(buf.readableBytes()), null)
                cachedIndexData = container.data
                buf.release()
            }
            data = cachedIndexData!!
        } else {
            val storage = filestore.storage as DiskStorage
            data = storage.readIndex(req.archive)
        }

        val response = FilestoreResponse(index = req.index, archive = req.archive, data = data)
        ctx.writeAndFlush(response)
    }

    private fun encodeFileData(ctx: ChannelHandlerContext, req: FilestoreRequest) {
        val index = filestore.findIndex(req.index)!!
        val archive = index.getArchive(req.archive)!!
        var data = filestore.storage.loadArchive(archive)

        if (data != null) {
            val compression = data[0]
            val length = getInt(data[1], data[2], data[3], data[4])
            val expectedLength = length + (if (compression.toInt() != CompressionType.NONE.ordinal) 9 else 5)
            if (expectedLength != length && data.size - expectedLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = FilestoreResponse(index = req.index, archive = req.archive, data = data)
            ctx.writeAndFlush(response)
        }
    }

    companion object {
        private var cachedIndexData: ByteArray? = null
        private fun getInt(b1: Byte, b2: Byte, b3: Byte, b4: Byte) =
            b1.toInt() shl 24 or (b2.toInt() and 0xff shl 16) or (b3.toInt() and 0xff shl 8) or (b4.toInt() and 0xff)
    }
}