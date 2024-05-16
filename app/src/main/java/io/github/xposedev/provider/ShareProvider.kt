package io.github.xposedev.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import io.github.xposedev.db.DBHelper


class ShareProvider : ContentProvider() {
    private val dbHelper by lazy { DBHelper(context!!) }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH) // uri匹配器

    // scope
    private val insertScopeCode = 10
    private val deleteScopeCode = 11
    private val queryScopeCode = 12

    // config
    private val updateConfigCode = 20
    private val queryConfigCode = 21

    override fun onCreate(): Boolean {
        // scope
        uriMatcher.addURI(ShareConstant.AUTHORITY, "/insert_scope", insertScopeCode)
        uriMatcher.addURI(ShareConstant.AUTHORITY, "/delete_scope", deleteScopeCode)
        uriMatcher.addURI(ShareConstant.AUTHORITY, "/query_scope", queryScopeCode)

        // config
        uriMatcher.addURI(ShareConstant.AUTHORITY, "/update_config", updateConfigCode)
        uriMatcher.addURI(ShareConstant.AUTHORITY, "/query_config", queryConfigCode)

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            queryScopeCode -> {
                dbHelper.queryScope(selection, selectionArgs)
            }

            queryConfigCode -> {
                dbHelper.queryConfig(selection, selectionArgs)
            }

            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            insertScopeCode -> {
                dbHelper.insertScope(values)
            }
        }

        context?.contentResolver?.notifyChange(uri, null)

        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.match(uri)) {
            deleteScopeCode -> {
                dbHelper.deleteScope(selection, selectionArgs)
            }

            else -> 0
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        return when (uriMatcher.match(uri)) {
            updateConfigCode -> {
                dbHelper.updateConfig(values, selection, selectionArgs)
            }

            else -> 0
        }
    }

    override fun shutdown() {
        dbHelper.close()
        super.shutdown()
    }
}