package io.github.xposedev.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import io.github.xposedev.db.entity.AppConfig
import io.github.xposedev.db.entity.AppScope

class ShareResolver(private val resolver: ContentResolver) {

    constructor(context: Context) : this(context.applicationContext.contentResolver)


    fun insertScopeByEntity(entity: AppScope): Uri? {
        val values = ContentValues()
        values.put("app_name", entity.appName)
        values.put("package_name", entity.packageName)
        values.put("application_name", entity.applicationName)

        return resolver.insert(ShareConstant.INSERT_SCOPE_URI, values)
    }

    fun deleteScopeByEntity(entity: AppScope): Int {
        val selection = StringBuilder().apply {
            append("app_name=?")
            append("package_name=?")
            append("application_name=?")
        }.toString()

        val selectionArgs = mutableListOf<String>().apply {
            add(entity.appName)
            add(entity.packageName)
            add(entity.applicationName)
        }.toTypedArray()

        return resolver.delete(
            ShareConstant.DELETE_SCOPE_URI,
            selection,
            selectionArgs,
        )
    }

    fun queryScope(
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
    ): MutableList<AppScope> {
        val appScopes = mutableListOf<AppScope>()
        resolver.query(
            ShareConstant.QUERY_SCOPE_URI,
            null,
            selection,
            selectionArgs,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    appScopes.add(
                        AppScope(
                            id = cursor.getInt(0),
                            appName = cursor.getString(1),
                            packageName = cursor.getString(2),
                            applicationName = cursor.getString(3),
                        )
                    )
                } while (cursor.moveToNext())
            }
        }

        return appScopes
    }

    fun queryScopeByEntity(
        entity: AppScope,
    ): MutableList<AppScope> {
        val selection = StringBuilder().apply {
            if (entity.appName.isNotEmpty()) {
                append("app_name=?")
            }
            if (entity.packageName.isNotEmpty()) {
                append("package_name=?")
            }
            if (entity.applicationName.isNotEmpty()) {
                append("application_name=?")
            }
        }.toString()

        val selectionArgs = mutableListOf<String>().apply {
            if (entity.appName.isNotEmpty()) {
                add(entity.appName)
            }
            if (entity.packageName.isNotEmpty()) {
                add(entity.packageName)
            }
            if (entity.applicationName.isNotEmpty()) {
                add(entity.applicationName)
            }
        }.toTypedArray()

        return queryScope(selection, selectionArgs)
    }

    fun updateConfig(
        values: ContentValues,
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
    ): Int {
        return resolver.update(
            ShareConstant.UPDATE_CONFIG_URI,
            values,
            selection,
            selectionArgs,
        )
    }

    fun updateConfigByEntity(
        entity: AppConfig,
    ): Int {
        val values = ContentValues().apply {
            if (entity.port.isNotEmpty()) {
                put("port", entity.port)
            }
        }

        return updateConfig(values, null, null)
    }

    fun queryConfig(
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
    ): MutableList<AppConfig> {
        val configs = mutableListOf<AppConfig>()
        resolver.query(
            ShareConstant.QUERY_CONFIG_URI,
            null,
            selection,
            selectionArgs,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    configs.add(
                        AppConfig(
                            port = cursor.getString(0),
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return configs
    }

    fun queryConfigByEntity(
        entity: AppConfig,
    ): MutableList<AppConfig> {
        val selection = StringBuilder().apply {
            if (entity.port.isNotEmpty()) {
                append("port=?")
            }
        }.toString()

        val selectionArgs = mutableListOf<String>().apply {
            if (entity.port.isNotEmpty()) {
                add(entity.port)
            }
        }.toTypedArray()

        return queryConfig(selection, selectionArgs)
    }
}