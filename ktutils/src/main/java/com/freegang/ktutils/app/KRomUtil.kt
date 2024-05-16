package com.freegang.ktutils.app

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.util.Locale

/**
 * link: https://gist.githubusercontent.com/TheMelody/5044dd1b697707b18e94b89f97f55db6/raw/1092635259c094fb843f3b7e5efb9daf6a0e6303/RomUtil.kt
 */
object KRomUtil {

    private const val TAG = "RomUtil"

    private const val HARMONY_OS = "harmony"

    private const val ROM_MIUI = "MIUI"
    private const val ROM_EMUI = "EMUI"
    private const val ROM_FLYME = "FLYME"
    private const val ROM_OPPO = "OPPO"
    private const val ROM_SMARTISAN = "SMARTISAN"
    private const val ROM_VIVO = "VIVO"
    private const val ROM_QIKU = "QIKU"

    private val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private val KEY_VERSION_EMUI = "ro.build.version.emui"
    private val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private val KEY_VERSION_VIVO = "ro.vivo.os.version"

    // 华为
    fun isEmui(): Boolean {
        return check(ROM_EMUI)
    }

    // 小米
    fun isMiui(): Boolean {
        return check(ROM_MIUI)
    }

    // vivo
    fun isVivo(): Boolean {
        return check(ROM_VIVO)
    }

    // oppo
    fun isOppo(): Boolean {
        return check(ROM_OPPO)
    }

    // 魅族
    fun isFlyme(): Boolean {
        return check(ROM_FLYME)
    }

    // 360
    fun isQiku(): Boolean {
        return check(ROM_QIKU) || check("360")
    }

    // 锤子手机
    fun isSmartisan(): Boolean {
        return check(ROM_SMARTISAN)
    }

    // 乐视
    fun isLetv(): Boolean = TextUtils.equals(Build.BRAND.toLowerCase(Locale.getDefault()), "letv")

    // 三星
    fun isSamsung(): Boolean = TextUtils.equals(Build.BRAND.toLowerCase(Locale.getDefault()), "samsung")

    // 一加
    fun onePlus(): Boolean = TextUtils.equals(Build.BRAND.toLowerCase(Locale.getDefault()), "oneplus")

    // 鸿蒙系统
    fun isHarmonyOS(): Boolean {
        try {
            val clz = Class.forName("com.huawei.system.BuildEx")
            val method: Method = clz.getMethod("getOsBrand")
            val classLoader = clz.classLoader

            // 如果BuildEx为系统提供的，其classloader为BootClassLoader
            // 如果BuildEx为伪造的，其classloader一般为PathClassLoader
            // Log.d(TAG, "classLoader: $classLoader")

            // BootClassLoader的parent为null
            if (classLoader != null && classLoader.parent == null) {
                return HARMONY_OS == method.invoke(clz)
            }
        } catch (_: ClassNotFoundException) {
        } catch (_: NoSuchMethodException) {
        } catch (_: Exception) {
        }
        return false
    }

    private var sName: String? = null

    private var sVersion: String? = null

    private fun check(rom: String): Boolean {
        if (sName != null) {
            return sName == rom
        }
        if (!TextUtils.isEmpty(getProp(KEY_VERSION_MIUI).also { sVersion = it })) {
            sName = ROM_MIUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_EMUI).also { sVersion = it })) {
            sName = ROM_EMUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_OPPO).also { sVersion = it })) {
            sName = ROM_OPPO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_VIVO).also { sVersion = it })) {
            sName = ROM_VIVO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_SMARTISAN).also {
                sVersion = it
            })) {
            sName = ROM_SMARTISAN
        } else {
            sVersion = Build.DISPLAY
            if (sVersion!!.toUpperCase(Locale.getDefault()).contains(ROM_FLYME)) {
                sName = ROM_FLYME
            } else {
                sVersion = Build.UNKNOWN
                sName = Build.MANUFACTURER.toUpperCase(Locale.getDefault())
            }
        }
        return sName == rom
    }

    private fun getProp(name: String): String? {
        val line: String?
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $name")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read prop $name", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(TAG, "BufferedReader.close()", e)
                }
            }
        }
        return line
    }
}