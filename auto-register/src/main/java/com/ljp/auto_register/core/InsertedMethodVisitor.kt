package com.ljp.auto_register.core

import com.ljp.auto_register.launch.AutoRegisterAsmVisitorClassFactory
import com.ljp.auto_register.util.ScanSetting
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File

class InsertedMethodVisitor(mv: MethodVisitor? = null) : MethodVisitor(Opcodes.ASM9,mv) {
    override fun visitInsn(opcode: Int) {
        //返回之前插入代码
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            AutoRegisterAsmVisitorClassFactory.registerList.forEach { ext->
                ext.classList.map{className->
                    className.replace('/','.').replace(File.separatorChar,'.')
                }
                .forEach { className->
                    println("插入代码：${className}")
                    mv?.visitLdcInsn(className)
                    mv?.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        ScanSetting.GENERATE_TO_CLASS_NAME,
                        ScanSetting.REGISTER_METHOD_NAME,
                        "(Ljava/lang/String;)V",
                        false
                        )
                }
            }
        }
        super.visitInsn(opcode)
    }
}