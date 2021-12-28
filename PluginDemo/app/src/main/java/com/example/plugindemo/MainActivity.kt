package com.example.plugindemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.core.annotation.LogInject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test(129)
        test2("小飞侠", -1)

        test3()
        t(1, 1.2f, null, 14L, JavaT())
        t2(false, 'A', 2, 2)
    }

    @LogInject(msg = "注解测试1", logType = LogInject.LOG_TYPE.ERROR)
    fun test(arg1: Int) {
    }

    @LogInject(msg = "注解测试2")
    fun test2(a: String, b: Int) {
    }

    @LogInject(msg = "基本数据类型（1）")
    fun t(it: Int, ft: Float, dt: Double?, ll: Long, t: JavaT?) {
        t?.jt0()
        t?.jte(it,"小力")

    }

    @LogInject(msg = "基本数据类型（2）")
    fun t2(bool: Boolean, ch: Char, byte: Byte, short: Short) {
    }

    @LogInject(msg = "注解测试3", logType = LogInject.LOG_TYPE.DEBUG)
    fun test3() {
    }
}