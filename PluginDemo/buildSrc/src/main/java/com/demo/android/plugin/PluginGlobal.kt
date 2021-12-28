package com.demo.android.plugin

/**
 * @className PluginGlobal
 * @description TODO
 * @author heyufei
 * @since 2021/12/16 2:54 下午
 * @version 1.0
 */
object PluginGlobal {
    private const val TAG = "HycanPlugin"

    fun printMsg(
        msg: String, tag: String = TAG
    ) {
        println("[$tag]===>($msg)")
    }

}