package io.github.xposedev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import io.github.xposedev.provider.ShareResolver
import io.github.xposedev.ui.XposeDevApp

val LocalShareResolver = compositionLocalOf<ShareResolver?>(structuralEqualityPolicy()) { null }

class MainActivity : ComponentActivity() {
    private val resolver: ShareResolver by lazy { ShareResolver(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                value = LocalShareResolver provides resolver,
            ) {
                XposeDevApp()
            }
        }
    }
}