package com.ljp.auto_register.core

import com.ljp.auto_register.util.ScanSetting
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 插入代码
 */
class InsertedClassVisitor(nextClassVisitor: ClassVisitor? = null) : ClassVisitor(Opcodes.ASM9,nextClassVisitor) {
    private  var currentClassName:String? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
//        val  targetName = ScanSetting.GENERATE_TO_CLASS_NAME.replace("/",".")
//        if (name == targetName ||name == ScanSetting.GENERATE_TO_CLASS_NAME) {
//            currentClassName = targetName
//            println("InsertedClassVisitor")
//
//        }
        super.visit(version, access, name, signature, superName, interfaces)
    }
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (/*currentClassName != null &&*/ name != null && name == ScanSetting.GENERATE_TO_METHOD_NAME) {
            return InsertedMethodVisitor(mv)
        }
        return mv
    }
}