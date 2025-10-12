package com.ljp.auto_register.core

import com.ljp.auto_register.launch.AutoRegisterAsmVisitorClassFactory
import com.ljp.auto_register.util.Logger
import com.ljp.auto_register.util.ScanSetting
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AutoRegisterAsmVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM9,nextClassVisitor) {
    private var currentClassName:String? = null
    private val implementedInterfaceNames:MutableList<String> = mutableListOf()
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        currentClassName = name
        interfaces?.let {
            implementedInterfaceNames.addAll(it)
        }
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitEnd() {
        currentClassName?.let { className->
            AutoRegisterAsmVisitorClassFactory.registerList.forEach { scanSetting ->
                implementedInterfaceNames.forEach { implementedInterface ->
                    if (implementedInterface.contains(scanSetting.interfaceName) && !scanSetting.classList.contains(className)) {
                        Logger.i("$className implements $implementedInterface, add to registerList")
                        println("添加到 registerList: $className")
                        scanSetting.classList.add(className)
                    }
                }
            }
        }
        super.visitEnd()
    }

}
