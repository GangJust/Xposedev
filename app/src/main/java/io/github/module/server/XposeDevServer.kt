package io.github.module.server

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.view.View
import com.freegang.ktutils.app.KNotifiUtils
import io.github.module.entity.CommandAndArgs
import io.github.module.helper.Helper
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

object XposeDevServer {
    private var app: Application? = null
    private var port: Int = 2345

    private val handler = Handler(Looper.getMainLooper())
    private val executors = Executors.newCachedThreadPool()
    private var server: ServerSocket? = null
    private val sockets = mutableListOf<Socket>()

    // 启动服务
    fun start(app: Application, port: Int = 2345) {
        this.app = app
        this.port = port

        try {
            server = ServerSocket(port)
            showNotification("listener port: $port")

            while (true) {
                val socket = server!!.accept()
                receiveMessage(socket)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 停止服务
    fun stop() {
        sockets.forEach {
            runCatching { it.close() }
        }
        sockets.clear()
        runCatching {
            server?.close()
        }
    }

    // 发送消息
    private fun sendMessage(msg: String) {
        // XplerLog.d("sendMessage: $msg")

        executors.execute {
            sockets.forEach { socket ->
                var writer: BufferedWriter? = null
                try {
                    writer = socket.getOutputStream().bufferedWriter()
                    writer.write(msg)
                    writer.newLine()
                    writer.flush()
                } catch (e: IOException) {
                    runCatching { writer?.close() }
                    runCatching { socket.close() }
                }
            }
        }
    }

    // 接收消息
    private fun receiveMessage(socket: Socket) {
        executors.execute {
            sockets.add(socket)
            var reader: BufferedReader? = null
            try {
                reader = socket.getInputStream().bufferedReader()
                while (true) {
                    val line = reader.readLine()
                    if (line == null || line == "exit") {
                        break
                    }
                    handleReceivedMessage(line)
                }
            } catch (e: IOException) {
                runCatching { socket.close() }
                sockets.remove(socket)
            }
        }
    }

    // 处理接收到的消息
    private fun handleReceivedMessage(msg: String) {
        showNotification("listener port: $port", msg)

        val commandAndArgs = CommandAndArgs.parse(msg)
        when (commandAndArgs.command) {
            "getActivity" -> {
                handler.post {
                    sendMessage(Helper.getActivity())
                }
            }

            "getLayout" -> {
                handler.post {
                    sendMessage(Helper.getLayout())
                }
            }

            "getFragment" -> {
                handler.post {
                    sendMessage(Helper.getFragment())
                }
            }

            "findViewById" -> {
                val first = commandAndArgs.args.firstOrNull()
                handler.post {
                    if (first == null) {
                        sendMessage("Please input ID.")
                    } else {
                        val id = if (first.startsWith("0x")) {
                            first.removePrefix("0x").toIntOrNull(16) ?: View.NO_ID
                        } else {
                            first.toIntOrNull() ?: View.NO_ID
                        }

                        if (id == View.NO_ID) {
                            sendMessage("The ID format is incorrect, it should be in the following formats: 0x123abc, 21310000.")
                        } else {
                            sendMessage(Helper.findViewById(id))
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(
        text: String,
        subText: String? = null,
    ) {
        KNotifiUtils.showNotification(
            context = app!!,
            notifyId = 789,
            channelId = "Server",
            channelName = "Server",
            title = "XposeDev",
            text = text,
            subText = subText,
        )
    }
}