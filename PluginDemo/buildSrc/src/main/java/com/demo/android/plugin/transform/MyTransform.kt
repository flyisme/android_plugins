package com.demo.android.plugin.transform

import com.didiglobal.booster.transform.Transformer
import com.demo.android.plugin.transform.impl.XlogAsmTransformer
import org.gradle.api.Project

/**
 * @className HycanTransform
 * @description TODO
 * @author heyufei
 * @since 2021/12/21 2:05 下午
 * @version 1.0
 */
class MyTransform(project: Project) : DoKitBaseTransform(project) {
    override val transformers = listOf<Transformer>(
        BaseDoKitAsmTransformer(
            listOf(XlogAsmTransformer())
        )
    )
}