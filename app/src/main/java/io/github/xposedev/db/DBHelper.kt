package io.github.xposedev.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context,
) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    1,
) {
    companion object {
        private const val DB_NAME = "xposedev"
        private const val TABLE_SCOPE = "app_scope"
        private const val TABLE_CONFIG = "app_config"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table_scope_sql = """
            CREATE TABLE $TABLE_SCOPE (
              id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
              app_name TEXT,
              package_name TEXT NOT NULL UNIQUE,
              application_name TEXT NOT NULL
            )
        """.trimIndent()

        val create_table_config_sql = """
            CREATE TABLE $TABLE_CONFIG (
              port TEXT NOT NULL
            )
        """.trimIndent()

        val insert_default_config_sql = """
            INSERT INTO $TABLE_CONFIG (
              port
            ) VALUES (
              "2345"
            )
        """.trimIndent()

        db?.execSQL(create_table_scope_sql)
        db?.execSQL(create_table_config_sql)
        db?.execSQL(insert_default_config_sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertScope(values: ContentValues?): Long {
        // return writableDatabase.use {db->
        //     db.insert(
        //         TABLE_SCOPE,
        //         null,
        //         values,
        //     )
        // }

        return writableDatabase.insert(
            TABLE_SCOPE,
            null,
            values,
        )
    }

    fun deleteScope(
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        // return writableDatabase.use {db->
        //     db.delete(
        //         TABLE_SCOPE,
        //         selection,
        //         selectionArgs,
        //     )
        // }

        return writableDatabase.delete(
            TABLE_SCOPE,
            selection,
            selectionArgs,
        )
    }

    fun queryScope(
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Cursor? {
        // return return readableDatabase.use { db->
        //     db.query(
        //         TABLE_SCOPE,
        //         null,
        //         selection,
        //         selectionArgs,
        //         null,
        //         null,
        //         null,
        //     )
        // }

        return readableDatabase.query(
            TABLE_SCOPE,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )
    }

    // config
    fun updateConfig(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        // return writableDatabase.use { db ->
        //     db.update(
        //         TABLE_CONFIG,
        //         values,
        //         selection,
        //         selectionArgs,
        //     )
        // }

        return writableDatabase.update(
            TABLE_CONFIG,
            values,
            selection,
            selectionArgs,
        )
    }

    fun queryConfig(
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Cursor? {
        // return readableDatabase.use { db ->
        //     db.query(
        //         TABLE_CONFIG,
        //         null,
        //         selection,
        //         selectionArgs,
        //         null,
        //         null,
        //         null,
        //     )
        // }

        return readableDatabase.query(
            TABLE_CONFIG,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )
    }
}