package com.demo.android.plugin.utils

import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.kotlinx.redirect
import com.didiglobal.booster.kotlinx.search
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.util.transform
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @className DoKitExtUtil
 * @description dokit 对象扩展(部分)
 * @author jint
 * @since 2021/12/20 3:31 下午
 * @version 1.0
 */

fun File.dokitTransform(
    output: File, transformer: (ByteArray) -> ByteArray = { it ->
        it
    }
) {
    when {
        isDirectory -> this.toURI().let { base ->
            if (this.exists()) {
                this.search().parallelStream().forEach {
                    it.transform(File(output, base.relativize(it.toURI()).path), transformer)
                }
            }
        }
        isFile -> when (extension.toLowerCase()) {
            "jar" -> JarFile(this).use {
                it.dokitTransform(output, ::JarArchiveEntry, transformer)
            }
            "class" -> this.inputStream().use {
                it.transform(transformer).redirect(output)
            }
            else -> this.copyTo(output, true)
        }
        else -> throw IOException("Unexpected file: ${this.canonicalPath}")
    }
}

fun ZipFile.dokitTransform(
    output: File,
    //:: 调用ZipArchiveEntry 构造函数：接受ZipEntry参数，返回 ZipArchiveEntry
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) = output.touch().outputStream().buffered().use {
    dokitTransform(it, entryFactory, transformer)
}


fun ZipFile.dokitTransform(
    output: OutputStream,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    val entries = mutableSetOf<String>()
    val creator = ParallelScatterZipCreator(
        ThreadPoolExecutor(
            NCPU,
            NCPU,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            RejectedExecutionHandler { runnable, _ ->
                runnable.run()
            })
    )

    //jar包文件序列化输出
    entries().asSequence().forEach { entry ->
        if (!entries.contains(entry.name)) {
            val zae = entryFactory(entry)
            val stream = InputStreamSupplier {
                when (entry.name.substringAfterLast('.', "")) {
                    "class" -> getInputStream(entry).use { src ->
                        try {
                            src.transform(transformer).inputStream()
                        } catch (e: Throwable) {
                            System.err.println("Broken class: ${this.name}!/${entry.name}")
                            getInputStream(entry)
                        }
                    }
                    else -> getInputStream(entry)
                }
            }

            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        } else {
            System.err.println("Duplicated jar entry: ${this.name}!/${entry.name}")
        }
    }

    //jar异常时不结束
    ZipArchiveOutputStream(output).use { zipStream ->
        try {
            creator.writeTo(zipStream)
            zipStream.close()
        } catch (e: Exception) {
            zipStream.close()
//            e.printStackTrace()
//            "e===>${e.message}".println()

            System.err.println("Duplicated jar entry: ${this.name}!")

        }
    }
}

