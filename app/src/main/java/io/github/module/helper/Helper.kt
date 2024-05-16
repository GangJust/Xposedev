package io.github.module.helper

import android.view.View
import com.freegang.extension.contentView
import com.freegang.extension.isEmpty
import com.freegang.extension.methodInvoke
import com.freegang.extension.toViewJson
import com.freegang.ktutils.app.KActivityUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.Stack

object Helper {

    fun getActivity(): String {
        val activities = KActivityUtils.getActivities()
        val array = JSONArray()
        activities.forEachIndexed { index, it ->
            array.put(JSONObject().apply {
                put("index", index)
                put("className", it.javaClass.name)
                put("contentView", "${it.contentView}")
                put("intentScheme", "${it.intent.scheme}")
                put("intentType", "${it.intent.type}")
                put("intentData", "${it.intent.data}")
                put("intentAction", "${it.intent.action}")
                put("intentCategories", "${it.intent.categories}")
                put("extras", "${it.intent.extras}")
            })
        }
        return if (array.isEmpty) {
            "No activity list."
        } else {
            array.toString()
        }
    }

    fun getLayout(): String {
        val activity = KActivityUtils.getActiveActivity()
        return activity?.contentView?.toViewJson() ?: "No active activities."
    }

    fun getFragment(): String {
        val activity = KActivityUtils.getActiveActivity()
        return if (activity == null) {
            "No active activities."
        } else {
            val fm = activity.methodInvoke("getSupportFragmentManager")
            if (fm == null) {
                "Unable to obtain FragmentManager"
            } else {
                val array = JSONArray()
                val fragments = fm.methodInvoke("getFragments") as? List<*>
                fragments?.forEachIndexed { index, fragment ->
                    val fragmentJson = JSONObject()
                    fragmentJson.put("index", index)
                    fragmentJson.put("className", "${fragment?.javaClass?.name}")
                    fragmentJson.put("context", "${fragment?.methodInvoke("getContext")}")
                    fragmentJson.put("visible", "${fragment?.methodInvoke("isVisible")}")
                    fragmentJson.put("instance", "$fragment")
                    fragmentJson.put("rootView", "${fragment?.methodInvoke("getView")}")
                    fragmentJson.put("arguments", "${fragment?.methodInvoke("getArguments")}")
                    val childFm = fragment?.methodInvoke("getChildFragmentManager") ?: return@forEachIndexed
                    fragmentJson.put("fragmentList", getFragmentsJson(childFm))
                    array.put(fragmentJson)
                }
                return array.toString()
            }
        }
    }

    fun findViewById(id: Int): String {
        val activity = KActivityUtils.getActiveActivity()
        return if (activity == null) {
            "No active activities."
        } else {
            activity.contentView.findViewById<View>(id)?.toViewJson() ?: "Not found id: ${id}."
        }
    }

    // 获取Fragment栈
    private fun getFragmentsJson(fm: Any): JSONArray {
        val array = JSONArray()

        // 创建存放JSONArray的栈
        val arrayStack = Stack<JSONArray>()
        arrayStack.push(array)

        // 存放FM的栈
        val fmStack = Stack<Any>()
        fmStack.push(fm)

        while (!fmStack.isEmpty()) {
            val currentArray = arrayStack.pop()
            val currentFm = fmStack.pop()

            val fragments = currentFm.methodInvoke("getFragments") as? List<*>
            fragments?.forEachIndexed { index, fragment ->
                val fragmentJson = JSONObject()
                fragmentJson.put("index", index)
                fragmentJson.put("className", "${fragment?.javaClass?.name}")
                fragmentJson.put("context", "${fragment?.methodInvoke("getContext")}")
                fragmentJson.put("visible", "${fragment?.methodInvoke("isVisible")}")
                fragmentJson.put("instance", "$fragment")
                fragmentJson.put("rootView", "${fragment?.methodInvoke("getView")}")
                fragmentJson.put("arguments", "${fragment?.methodInvoke("getArguments")}")
                val childArray = JSONArray()
                val childFm = fragment?.methodInvoke("getChildFragmentManager") ?: return@forEachIndexed
                fragmentJson.put("fragmentList", childArray)
                currentArray.put(fragmentJson)

                arrayStack.push(childArray)
                fmStack.push(childFm)
            }
        }

        return array
    }
}