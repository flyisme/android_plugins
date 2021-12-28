package com.demo.android.plugin.task

import com.demo.android.plugin.utils.Log

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @className RemoveResourcesTask
 * @description TODO
 * @author heyufei
 * @since 2021/12/24 9:29 上午
 * @version 1.0
 */
class RemoveResourcesTask : DefaultTask() {
    private val TAG = "RemoveResourcesTask"

    val configFileName = "unused_resources_config.json"

    //配置文件
    val file = File(project.rootDir.path + "/gradle_src/config/" + configFileName)

    @TaskAction
    fun remove_unused_resources() {

        val job = readFile(file)
        if (job != null) {
            val unused_resources = job.get("unused_resources") as List<String>
            val directory_white_list = job.get("directory_white_list") as List<String>
            val file_white_list = job.get("file_white_list") as List<String>


            //未使用的图片
            val unUserPics = ArrayList<String>()
            //未使用的布局
            val unUserLayouts = ArrayList<String>()
            val directoryWhiteList = ArrayList<String>()
            for (i in 0..directory_white_list.size) {
                directoryWhiteList.add(directory_white_list[i])
            }

            //匹配资源文件名（不包含文件类型:eg: "xxx.jpg"集合保存"xxx"）
            for (i in 0..unused_resources.size) {
                var item = unused_resources[i]
                when {
                    item.startsWith("R.drawable.") -> {
                        unUserPics.add(item.replaceFirst("R.drawable.", "") + ".")
                    }
                    item.startsWith("R.mipmap.") -> {
                        unUserPics.add(item.replaceFirst("R.mipmap.", "") + ".")
                    }
                    item.startsWith("R.layout.") -> {
                        unUserLayouts.add(item.replaceFirst("R.layout.", "") + ".")
                    }
                }
            }

            //移除白名单文件
            for (i in 0..file_white_list.size) {
                unUserPics.remove(file_white_list.get(i))
                unUserLayouts.remove(file_white_list.get(i))
            }
            Log.d(TAG, "开始移除未使用资源")


            var delPicNum = 0

            for (i in 0..unUserPics.size) {
                var files = searchFile(project.rootDir, unUserPics.get(i), directoryWhiteList)
                if (files.isEmpty()) {
                    Log.d(TAG, "++++++++++ " + unUserPics.get(i) + " 未找到符合的文件")
                } else {
                    Log.d(TAG, "++++++++++ " + unUserPics.get(i) + " 找到" + files.size + "个，开始删除：")
                    delPicNum += files.size
                    deleteFiles(files)
                }
            }

            Log.d(TAG, "已删除：图片：" + delPicNum)
            var delLayoutNum = 0

            for (i in 0..unUserLayouts.size) {
                var files = searchFile(project.rootDir, unUserLayouts.get(i), directoryWhiteList)
                if (files.isEmpty()) {
                    Log.d(TAG, "++++++++++ " + unUserLayouts.get(i) + " 未找到符合的文件")
                } else {
                    Log.d(
                        TAG,
                        "++++++++++ " + unUserLayouts.get(i) + " 找到" + files.size + "个，开始删除："
                    )
                    delLayoutNum += files.size
                    deleteFiles(files)

                }
            }
            Log.d(TAG, "已删除：布局文件：" + delLayoutNum)

            Log.d(TAG, "共删除：图片：" + delPicNum + "，布局文件：" + delLayoutNum)


        }
    }


    /**
     * 文件递搜索
     * @param startDir 开始搜索的根目录
     * @param file_name 期望搜索的文件名，不包括后缀
     * @param directoryWhiteList 文件目录白名单(不会搜索该目录)
     * @return
     */
    fun searchFile(
        startDir: File,
        file_name: String,
        directoryWhiteList: List<String>
    ): List<File> {
        val result = ArrayList<File>();
        if (startDir.listFiles() == null) {//防止主函数调用时给出路径不是一个有效的目录
            return result;//直接返回空的list
        }

        for (item in startDir.listFiles()) {
            if (item.isDirectory) {
                //不在白名单
                if (!directoryWhiteList.contains(item.name)) {
                    result.addAll(searchFile(item, file_name, directoryWhiteList))
                }

            } else {
                if (item.name.startsWith(file_name)) {
                    result.add(item)
                }
            }
        }
        return result
    }

    /**
     * json 文件读取
     * @param file
     * @return
     */
    fun readFile(file: File): Map<String, Any>? {
        var job: Map<String, Any>? = null

        if (file.exists()) {
            val jsonSluper = JsonSlurper()
            job = jsonSluper.parse(file) as Map<String, Any>
        }
        return job
    }

    fun deleteFiles(files: List<File>) {
        for (i in 0..files.size) {
            if (files[i].exists()) {
                Log.d(TAG, "-----删除--(" + i + ")--文件： " + files.get(i).path)
//                files[i].delete()
            }
        }
    }
}