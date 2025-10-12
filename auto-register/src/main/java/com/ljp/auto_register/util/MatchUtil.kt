package com.ljp.auto_register.util

import com.ljp.auto_register.launch.AutoRegisterAsmVisitorClassFactory
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.InputStream

object MatchUtil {
    fun isImplement(classInputStream:InputStream) : Boolean {
        var witchImplement = false
        val byteArray = classInputStream.readAllBytes()
        try {
            val classReader = ClassReader(byteArray)
            val classVisitor = object : ClassVisitor(Opcodes.ASM9) {
                override fun visit(
                    version: Int,
                    access: Int,
                    name: String?,
                    signature: String?,
                    superName: String?,
                    interfaces: Array<out String>?
                ) {
                    AutoRegisterAsmVisitorClassFactory.registerList.forEach { ext->
                        interfaces?.forEach { implement ->
                            if (implement.contains(ext.interfaceName)) {
                                witchImplement = true
                                ext.classList.add(implement)
                            }
                        }
                    }
                    super.visit(version, access, name, signature, superName, interfaces)
                }
            }
            classReader.accept(classVisitor,ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        }catch (e:Exception){
            println("警告：解析失败 $e")
        }
        return witchImplement
    }
}