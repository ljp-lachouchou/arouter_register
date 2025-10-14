package com.ljp.auto_register.launch

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationContext
import com.android.build.api.instrumentation.InstrumentationParameters
import com.ljp.auto_register.core.AutoRegisterAsmVisitor
import com.ljp.auto_register.core.InsertedClassVisitor
import com.ljp.auto_register.util.Logger
import com.ljp.auto_register.util.ScanSetting
import org.apache.tools.ant.taskdefs.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor


abstract class AutoRegisterAsmVisitorClassFactory : AsmClassVisitorFactory<AutoRegisterAsmVisitorClassFactory.Parameters>{
    interface Parameters : InstrumentationParameters {

    }
    companion object {
        val registerList = mutableSetOf<ScanSetting>()
    }
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
//        println("ljp.ARouter::Scan ${classContext.currentClassData.className}")
        return  AutoRegisterAsmVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        val  isExcluded  =  (className.contains("android.") && !className.contains("arouter")) ||
                // 排除资源类 R
                className.endsWith(".R") ||
                // 排除其他系统/框架类（前缀匹配更精确）
                className.startsWith("androidx.") ||
                className.startsWith("kotlin.") ||
                className.startsWith("java.") ||
                className.startsWith("kotlinx.")
        return  !isExcluded
    }
}