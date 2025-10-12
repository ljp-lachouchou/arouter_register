package com.ljp.auto_register.util

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Logger {
    lateinit var logger:Logger
    fun make(project:Project) {
        logger = project.logger
    }
    fun i(info:String) {
        logger.info("ljp.ARouter::Register i >>> $info")
    }
    fun e(error:String) {
        logger.error("ljp.ARouter::Register e >>> $error")
    }

    fun  w(warn:String) {
        logger.warn("ljp.ARouter::Register w >>> $warn")
    }


}