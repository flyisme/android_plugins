package com.demo.android.plugin.transform.impl

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.didiglobal.booster.transform.asm.isStatic
import com.demo.android.plugin.utils.Log
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * @className XlogAsmTransformer
 * @description
 * Xlog 注解代码添加
 * @author heyufei
 * @since 2021/12/21 11:20 上午
 * @version 1.0
 */
class XlogAsmTransformer : ClassTransformer {


    private val TAG = "XlogAsmTransformer"
    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        val className = klass.className
        val blackList = getBlackList()
        //过滤黑名单
        if (blackList.isNotEmpty()) {
            if (blackList.contains(className)) {
                return klass
            }
        }
        //查找注解标注的方法
        klass.methods?.filter { node ->
            null != node.invisibleAnnotations?.find { it.desc == "Lcom/example/core/annotation/LogInject;" }
        }?.forEach { methodNode ->
            //插桩
            methodNode.instructions.insert(createLogCode(klass, methodNode))
        }

        return klass
    }

    private fun createLogCode(klass: ClassNode, method: MethodNode): InsnList {
        val logInjectAnnotation =
            method.invisibleAnnotations!!.find { it.desc == "Lcom/example/core/annotation/LogInject;" }!!
        var debugType = "DEBUG"
        var debugMsg: String? = null
        //注解参数解析
        logInjectAnnotation.values.forEachIndexed { index, item ->
            if ("logType" == item) {
                debugType = (logInjectAnnotation.values[1 + index] as Array<String>)[1]
            } else if ("msg" == item) {
                debugMsg = logInjectAnnotation.values[1 + index] as String
            }
        }


        if (debugMsg == null) {
            throw Exception("注解msg不可以为null")
        }
        Log.d(
            TAG, "找到符合的注解 ${method.name},参数解析完毕：debugType=$debugType,debugMsg=$debugMsg"
        )
        return InsnList().apply {

            val simpleName = klass.className.substring(klass.className.lastIndexOf("."))
            //日志 TAG
            add(LdcInsnNode("CoreLog$simpleName"))
            //日志 value
            add(TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"))
            add(InsnNode(Opcodes.DUP))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"
                )
            )
            add(LdcInsnNode("(${method.name})[$debugMsg],args:["))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                )
            )
            //方法参数
            var addIndex = 0
            method.localVariables?.forEachIndexed { index, item ->
                if (method.isStatic || item.name != "this") {
                    add(LdcInsnNode(",${item.name}:"))
                    add(
                        MethodInsnNode(
                            Opcodes.INVOKEVIRTUAL,
                            "java/lang/StringBuilder",
                            "append",
                            "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                        )
                    )
                    Log.i(TAG, "${item.name},${item.desc},$index")
                    //不同参数类型执行不同操作指令

                    //递增index
                    when (item.desc) {
                        "I", "B", "S" -> {
                            //int 类型
                            add(
                                VarInsnNode(
                                    Opcodes.ILOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(I)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        "Z" -> {
                            //boolean 类型
                            add(
                                VarInsnNode(
                                    Opcodes.ILOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(Z)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        "C" -> {
                            //char
                            add(
                                VarInsnNode(
                                    Opcodes.ILOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(C)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        "F" -> {
                            //float 类型
                            add(
                                VarInsnNode(
                                    Opcodes.FLOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(F)Ljava/lang/StringBuilder;"
                                )
                            )
                        }

                        "D" -> {
                            //double
                            add(
                                VarInsnNode(
                                    Opcodes.DLOAD, index + addIndex++
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(D)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        "J" -> {
                            //long
                            add(
                                VarInsnNode(
                                    Opcodes.LLOAD, index + addIndex++
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(J)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        "Ljava/lang/String;" -> {
                            add(
                                VarInsnNode(
                                    Opcodes.ALOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                                )
                            )
                        }
                        else -> {
                            //非基本类型
                            add(
                                VarInsnNode(
                                    Opcodes.ALOAD, index + addIndex
                                )
                            )
                            add(
                                MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuilder",
                                    "append",
                                    "(Ljava/lang/Object;)Ljava/lang/StringBuilder;"
                                )
                            )
                        }

                    }
                }
            }
            add(LdcInsnNode("]"))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "toString",
                    "()Ljava/lang/String;"
                )
            )



            if ("ERROR" == debugType) {
                add(InsnNode(Opcodes.ACONST_NULL))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "android/util/Log",
                        "e", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I"
                    )
                )
            } else {
                add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "android/util/Log",
                        when (debugType) {
                            "DEBUG" -> "d"
                            "INFO" -> "i"
                            "WARN" -> "w"
                            else -> "d"
                        }, "(Ljava/lang/String;Ljava/lang/String;)I"
                    )
                )
            }
            add(InsnNode(Opcodes.POP))
        }
    }

    private fun getBlackList(): ArrayList<String> {
        return ArrayList<String>().apply {
            add("com.example.core.annotation.CoreLogInject\$LOG_TYPE")
            add("com.example.core.annotation.CoreLogInject")
        }
    }
}