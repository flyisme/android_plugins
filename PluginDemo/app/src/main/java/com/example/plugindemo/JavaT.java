package com.example.plugindemo;


import android.util.Log;

import com.example.core.annotation.LogInject;


/**
 * @author heyufei
 * @version 1.0
 * @className JavaT
 * @description TODO
 * @since 2021/12/22 3:41 下午
 */
public class JavaT {
    @LogInject(msg = "heheh")
    void jt0() {

    }

    @LogInject(msg = "错误：：：：", logType = LogInject.LOG_TYPE.ERROR)
    void jte(int a0, String name) {

    }

    @LogInject(msg = "全参数测试：:----：：", logType = LogInject.LOG_TYPE.ERROR)
    void jte2(int it, float ft, double dt, long l, boolean b, char c, byte bb, short ss) {
        Log.e("应该是", "jte2: int:" + it + ",float" + ft + ",double:" + dt + ",long:" + l + ",boolean" + b + ",char:" + c + ",byte:" + bb + ",short:" + ss, null);
    }
}
