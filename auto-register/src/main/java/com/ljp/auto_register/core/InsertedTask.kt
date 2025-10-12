package com.ljp.auto_register.core

import com.ljp.auto_register.launch.AutoRegisterAsmVisitorClassFactory
import com.ljp.auto_register.util.MatchUtil
import com.ljp.auto_register.util.ScanSetting
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class InsertedTask : DefaultTask() {
    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jars : ListProperty<RegularFile> //之前的jar查看

    @get:Incremental
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val dirs : ListProperty<Directory> //目录查看
    @get:OutputFile
    abstract val output:RegularFileProperty //输出的jar
    @TaskAction
    fun taskAction(inputChanges: InputChanges) {
        println("输出 JAR 路径：${output.get().asFile.absolutePath}")
        var needOperateByteArray:ByteArray? = null
        JarOutputStream(FileOutputStream(output.get().asFile)).use { jarOutput->
            processTargetJars(jarOutput) {
                needOperateByteArray = it
            }
            if (needOperateByteArray == null) {
                error("未找到目标类 ${ScanSetting.GENERATE_TO_CLASS_FILE_NAME}，无法注入代码")
            }
            processDirs(jarOutput)
            jarOutput.putNextEntry(JarEntry(ScanSetting.GENERATE_TO_CLASS_FILE_NAME))
            val input = ByteArrayInputStream(needOperateByteArray)
            val reader = ClassReader(input)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            val visitor = InsertedClassVisitor(classWriter)
            reader.accept(visitor,ClassReader.EXPAND_FRAMES) //注入代码时使用
            jarOutput.write(classWriter.toByteArray())
            input.use { inputStream ->
                inputStream.copyTo(jarOutput)
            }
            jarOutput.closeEntry()
        }
    }
    private fun processTargetJars(jarOutput:JarOutputStream,needOperateListener:(ByteArray)->Unit) {
        jars.get().forEach { file ->
            JarFile(file.asFile).use { jarFile->
                jarFile.entries().asIterator().forEach { entry->
                    if (!entry.isDirectory && entry.name.contains(ScanSetting.GENERATE_TO_CLASS_FILE_NAME)) {
//                        println("jar entry target ${entry.name}")
                        jarFile.getInputStream(entry).use {
                            needOperateListener(it.readAllBytes())
                        }
                    }
                    else {
                        kotlin.runCatching {
                            jarFile.getInputStream(entry).use {
//                                if (MatchUtil.isImplement(it)) {
//                                    println("jar scan entry ${entry.name}")
//                                }
                                jarOutput.putNextEntry(JarEntry(entry.name))
                                it.copyTo(jarOutput)
                            }
                        }
                        jarOutput.closeEntry()
                    }
                }
            }
        }
    }

    private fun processDirs(jarOutput: JarOutputStream) {
        dirs.get().forEach { dir ->
            dir.asFile.walk().forEach { file->
                if (file.isFile) {
                    val relativePath = dir.asFile.toURI().relativize(file.toURI()).path
//                    println("jar file relativePath: $relativePath")
                    val  entryName = relativePath.replace(File.separatorChar, '/')
//                    println("jar file entryName: $entryName")
                    jarOutput.putNextEntry(JarEntry(entryName))
                    file.inputStream().use {
//                        if (MatchUtil.isImplement(it)) {
//                            println("jar scan file  $relativePath")
//                        }
                        it.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                }
            }
        }
    }
}