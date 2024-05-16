package io.github.module

import android.app.AndroidAppHelper
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.module.server.XposeDevServer
import io.github.xpler.HookEntrance
import io.github.xpler.core.hookClass
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.thisApplication
import io.github.xpler.core.wrapper.DefaultHookStart
import io.github.xposedev.aidl.IMessageCallback
import io.github.xposedev.aidl.IWakeService
import io.github.xposedev.provider.ShareResolver
import java.util.concurrent.Executors

class XposeDevInitiator : HookEntrance<XposeDevInitiator>(), DefaultHookStart {
    private val executors = Executors.newSingleThreadExecutor()
    private var wakeService: IWakeService? = null
    private var messageCallback: IMessageCallback.Stub? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            XplerLog.d("onServiceConnected: name=$name, service=$wakeService")

            wakeService = IWakeService.Stub.asInterface(service)
            wakeService?.rgigster(object : IMessageCallback.Stub() {
                override fun callback(tag: String?, message: String?) {
                    XplerLog.d("$tag: $message")
                }
            }.also { messageCallback = it })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            XplerLog.d("onServiceDisconnected: name=$name")

            wakeService?.unregister(messageCallback)
            wakeService = null
            messageCallback = null
        }
    }

    private fun startServer() {
        executors.execute {
            // wait for service start
            Thread.sleep(500L)

            val app = AndroidAppHelper.currentApplication()

            // resolve share content
            val shareResolver = ShareResolver(app)

            // filter scope
            /* val scope = shareResolver.queryScope()
            val filter = scope.filter {
                it.packageName == app.packageName
                        && it.applicationName == app.javaClass.name
            }
            if (filter.isEmpty())
                return@onAfter */

            // start service
            val config = shareResolver.queryConfig().firstOrNull()
            val port = config?.port?.toIntOrNull() ?: 2345
            XposeDevServer.start(app, port)

            // log
            XplerLog.d(
                "-- xposedev patch",
                "-- port: $port",
                "-- package: ${app.packageName}",
                "-- applicationName: ${app.javaClass.name}",
                "-- processName: ${AndroidAppHelper.currentProcessName()}",
            )
        }
    }

    override fun loadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XplerLog.setTag("XposeDev")
        XplerLog.isXposed(false)

        lpparam.hookClass(Application::class.java)
            .method("attach", Context::class.java) {
                onAfter {
                    val app = thisApplication

                    val intent = Intent().apply {
                        setAction("$modulePackage.service.WakeService")
                        setClassName(modulePackage, "$modulePackage.service.WakeService")
                    }
                    app.bindService(intent, connection, Context.BIND_AUTO_CREATE)

                    startServer()
                }
            }
    }

    override val modulePackage: String
        get() = "io.github.xposedev"
}