package com.lelular.consumodocarro.ui.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxWidth
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.lelular.consumodocarro.BuildConfig

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // Automaticamente usa ID de teste em DEBUG e ID de produção em RELEASE
                adUnitId = if (BuildConfig.DEBUG) {
                    "ca-app-pub-3940256099942544/6300978111" // Test ID (evita ban durante desenvolvimento)
                } else {
                    "ca-app-pub-9749532540376463/1554403905" // Production ID (usado em release)
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
