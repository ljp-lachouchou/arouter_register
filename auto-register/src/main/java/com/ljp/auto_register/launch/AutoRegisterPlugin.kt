package com.ljp.auto_register.launch

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.ljp.auto_register.core.InsertedTask
import com.ljp.auto_register.util.Logger
import com.ljp.auto_register.util.ScanSetting
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoRegisterPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        Logger.make(target)
        println("ljp.ARouter::Register apply")
        val androidComponentsExtension = target.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponentsExtension.onVariants { variant ->
            println("ljp.ARouter::Register ${AutoRegisterAsmVisitorClassFactory.registerList.size}")
            AutoRegisterAsmVisitorClassFactory.registerList.add(ScanSetting("IRouteRoot"))
            AutoRegisterAsmVisitorClassFactory.registerList.add(ScanSetting("IInterceptorGroup"))
            AutoRegisterAsmVisitorClassFactory.registerList.add(ScanSetting("IProviderGroup"))
            println("ljp.ARouter::Register ${AutoRegisterAsmVisitorClassFactory.registerList.size}")
//            variant.instrumentation.transformClassesWith(
//                AutoRegisterAsmVisitorClassFactory::class.java,
//                InstrumentationScope.ALL
//            ) {}
//            variant.instrumentation.transformClassesWith(
//                InsertedAsmVisitorClassFactory::class.java,
//                InstrumentationScope.ALL
//            ) {
//            }
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
        androidComponentsExtension.onVariants { variant ->
            val taskProviderTransformAllClassesTask = target.tasks.register("${variant.name}InsertedTask",
                InsertedTask::class.java)
            variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(taskProviderTransformAllClassesTask)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    InsertedTask::jars,
                    InsertedTask::dirs,
                    InsertedTask::output
                    )
        }
    }
}