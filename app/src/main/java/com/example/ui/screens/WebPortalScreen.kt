package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Surface(
            color = DarkNavy,
            border = BorderStroke(1.dp, BorderSlate),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("web_portal_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RoyalGold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "KINGS EARNS Website",
                            style = MaterialTheme.typography.titleMedium,
                            color = RoyalGold
                        )
                        Text(
                            text = "Official App Download Website",
                            style = MaterialTheme.typography.labelSmall,
                            color = CoolSilver
                        )
                    }
                }
                Surface(
                    color = ElevatedNavy,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            tint = RoyalGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WEB PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("web_portal_webview"),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    @Suppress("DEPRECATION")
                    settings.allowFileAccessFromFileURLs = false
                    @Suppress("DEPRECATION")
                    settings.allowUniversalAccessFromFileURLs = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val uri = request?.url ?: return false
                            val scheme = uri.scheme?.lowercase()
                            if (scheme == "file" && uri.path?.startsWith("/android_asset/website/") == true) {
                                return false
                            }
                            if (scheme == "mailto" || scheme == "https" || scheme == "http") {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                                return true
                            }
                            return false
                        }
                    }
                    loadUrl("file:///android_asset/website/index.html")
                }
            }
        )
    }
}
