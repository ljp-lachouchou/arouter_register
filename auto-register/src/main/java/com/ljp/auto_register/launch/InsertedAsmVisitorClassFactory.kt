package com.ljp.auto_register.launch

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.ljp.auto_register.core.InsertedClassVisitor
import com.ljp.auto_register.util.ScanSetting
import org.objectweb.asm.ClassVisitor

abstract class InsertedAsmVisitorClassFactory : AsmClassVisitorFactory<AutoRegisterAsmVisitorClassFactory.Parameters> {
    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        val  targetName = ScanSetting.GENERATE_TO_CLASS_NAME.replace("/",".")
        val  isExcluded  =  (className.contains("android/") && !className.contains("arouter")) ||
                // 排除资源类 R
                className.endsWith("/R") ||
                // 排除其他系统/框架类（前缀匹配更精确）
                className.startsWith("androidx/") ||
                className.startsWith("kotlin/") ||
                className.startsWith("java/")
        return  className == targetName && !isExcluded
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val  targetName = ScanSetting.GENERATE_TO_CLASS_NAME.replace("/",".")
        if (classContext.currentClassData.className == targetName) {
            println("InsertedClassVisitor")
            return InsertedClassVisitor(nextClassVisitor)
        }
        return nextClassVisitor
    }
}