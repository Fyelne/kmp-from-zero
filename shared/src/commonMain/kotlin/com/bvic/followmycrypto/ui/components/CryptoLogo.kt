package com.bvic.followmycrypto.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bvic.followmycrypto.model.Crypto

@Composable
fun CryptoLogo(
    crypto: Crypto,
    modifier: Modifier = Modifier
) {
    var isLoaded by remember(crypto.logoUrl) {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!isLoaded) {
            Text(crypto.symbol.take(2).uppercase())
        }

        AsyncImage(
            model = crypto.logoUrl,
            contentDescription = "Logo de ${crypto.name}",
            modifier = Modifier.fillMaxSize(),
            onSuccess = { isLoaded = true },
            onError = { isLoaded = false }
        )
    }
}