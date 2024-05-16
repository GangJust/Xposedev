package io.github.xposedev.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import io.github.xpler.core.log.XplerLog
import io.github.xposedev.aidl.IMessageCallback
import io.github.xposedev.aidl.IWakeService

class WakeService : Service() {
    private val binder = Binder()
    private var count = 0

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        XplerLog.d("WakeService onCreated..")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        XplerLog.d("WakeService onStartCommand..")
        return super.onStartCommand(intent, flags, startId)
    }

    inner class Binder : IWakeService.Stub() {
        private var _callback: RemoteCallbackList<IMessageCallback?> = RemoteCallbackList()

        override fun rgigster(callback: IMessageCallback?) {
            this._callback.register(callback)
        }

        override fun unregister(callback: IMessageCallback?) {
            this._callback.unregister(callback)
        }

        override fun sendMessage(tag: String?, message: String?) {
            for (i in 0 until this._callback.registeredCallbackCount) {
                this._callback.getBroadcastItem(i)?.callback(tag, message)
            }
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?,
        ) {

        }
    }
}