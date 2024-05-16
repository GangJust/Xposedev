package io.github.xposedev.provider

import android.net.Uri

object ShareConstant {
    const val AUTHORITY: String = "io.github.xposedev.provider"

    // scope
    val INSERT_SCOPE_URI: Uri = Uri.parse("content://$AUTHORITY/insert_scope")
    val DELETE_SCOPE_URI: Uri = Uri.parse("content://$AUTHORITY/delete_scope")
    val QUERY_SCOPE_URI: Uri = Uri.parse("content://$AUTHORITY/query_scope")

    // config
    val UPDATE_CONFIG_URI: Uri = Uri.parse("content://$AUTHORITY/update_config")
    val QUERY_CONFIG_URI: Uri = Uri.parse("content://$AUTHORITY/query_config")
}