package com.ljp.auto_register.util

import java.io.File

class ScanSetting(val interfaceName:String) {
    companion object {
        const val PLUGIN_NAME = "com.alibaba.arouter"
        const val GENERATE_TO_CLASS_NAME = "com/alibaba/android/arouter/core/LogisticsCenter"
        const val GENERATE_TO_CLASS_FILE_NAME = "$GENERATE_TO_CLASS_NAME.class"
        const val GENERATE_TO_METHOD_NAME = "loadRouterMap"
        const val ROUTER_CLASS_PACKAGE_NAME = "com/alibaba/android/arouter/routes/"
        const val INTERFACE_PACKAGE_NAME = "com/alibaba/android/arouter/facade/template/"
        const val REGISTER_METHOD_NAME = "register"

    }
    /**
     * jar file which contains class: GENERATE_TO_CLASS_FILE_NAME
     */
    var fileContainsInitClass: File? = null
    /**
     * scan result for {@link #interfaceName}
     * class names in this list
     */
    val classList: MutableSet<String> = mutableSetOf()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as ScanSetting
        if (interfaceName != other.interfaceName) return false
        return true
    }

    override fun hashCode(): Int {
        return interfaceName.hashCode()
    }

}