package com.bvic.followmycrypto.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bvic.followmycrypto.data.AppContainer
import com.bvic.followmycrypto.model.Crypto
import com.bvic.followmycrypto.theme.FollowMyCryptoTheme
import com.bvic.followmycrypto.theme.PriceDown
import com.bvic.followmycrypto.theme.PriceUp
import com.bvic.followmycrypto.ui.components.ErrorContent
import com.bvic.followmycrypto.ui.components.LoadingContent
import com.bvic.followmycrypto.ui.format.formatPercent
import com.bvic.followmycrypto.ui.format.formatPrice
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.bvic.followmycrypto.ui.components.CryptoLogo
import org.jetbrains.compose.resources.stringResource

@Composable
fun CryptoListScreen(
    onCryptoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoListViewModel = viewModel {
        CryptoListViewModel(
            AppContainer.cryptoRepository,
            AppContainer.favoriteRepository,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()

    CryptoListContent(
        uiState = uiState,
        favorites = favorites,
        favoritesOnly = favoritesOnly,
        onRefresh = viewModel::refresh,
        onCryptoClick = onCryptoClick,
        onToggleFavorite = viewModel::toggleFavorite,
        onToggleFavoritesFilter = viewModel::toggleFavoritesFilter,
        modifier = modifier,
    )
}

// Version sans état : elle ne connaît pas le ViewModel, on peut donc l'afficher dans un aperçu
@Composable
fun CryptoListContent(
    uiState: CryptoListUiState,
    favorites: Set<String>,
    favoritesOnly: Boolean,
    onRefresh: () -> Unit,
    onCryptoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FollowMyCrypto", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onToggleFavoritesFilter) {
                        Icon(
                            imageVector = if (favoritesOnly) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Filled.FavoriteBorder
                            },
                            contentDescription = if (favoritesOnly) {
                                "Afficher toutes les cryptos"
                            } else {
                                "Afficher uniquement les favoris"
                            },
                            tint = if (favoritesOnly) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }

                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Rafraîchir")
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding).fillMaxSize()
        when (uiState) {
            CryptoListUiState.Loading -> LoadingContent(modifier = contentModifier)
            is CryptoListUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRefresh,
                modifier = contentModifier,
            )
            is CryptoListUiState.Success -> CryptoList(
                cryptos = uiState.cryptos,
                favorites = favorites,
                onCryptoClick = onCryptoClick,
                onToggleFavorite = onToggleFavorite,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
fun CryptoList(
    cryptos: List<Crypto>,
    favorites: Set<String>,
    onCryptoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = cryptos, key = { it.symbol }) { crypto ->
            CryptoRow(
                crypto = crypto,
                isFavorite = crypto.symbol in favorites,
                onClick = { onCryptoClick(crypto.symbol) },
                onToggleFavorite = {
                    onToggleFavorite(crypto.symbol)
                },
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun CryptoRow(
    crypto: Crypto,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.padding(end = 8.dp)) {
            CryptoLogo(crypto = crypto, modifier = Modifier.size(40.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = crypto.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = crypto.symbol,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatPrice(crypto.price),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = formatPercent(crypto.changePercent),
                style = MaterialTheme.typography.bodySmall,
                color = if (crypto.changePercent >= 0) PriceUp else PriceDown,
            )
        }
        Column {
            IconButton(
                onClick = onToggleFavorite,
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) {
                        "Retirer des favoris"
                    } else {
                        "Ajouter aux favoris"
                    }
                )
            }
        }
    }
}

private val previewCryptos = listOf(
    Crypto(
        symbol = "BTC",
        name = "Bitcoin",
        price = 85613.50,
        highPrice = 85666.50,
        lowPrice = 10000.00,
        changePercent = 0.88),
    Crypto(
        symbol = "ETH",
        name = "Ethereum",
        price = 3214.07,
        highPrice = 99999.99,
        lowPrice = 11111.11,
        changePercent = -1.42),
)

@Preview(showBackground = true)
@Composable
fun CryptoRowPreview() {
    FollowMyCryptoTheme {
        CryptoRow(
            crypto = previewCryptos.first(),
            isFavorite = true,
            onClick = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoListContentPreview() {
    FollowMyCryptoTheme {
        CryptoListContent(
            uiState = CryptoListUiState.Success(previewCryptos),
            favorites = setOf("BTC"),
            favoritesOnly = false,
            onRefresh = {},
            onCryptoClick = {},
            onToggleFavorite = {},
            onToggleFavoritesFilter = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoListErrorPreview() {
    FollowMyCryptoTheme {
        CryptoListContent(
            uiState = CryptoListUiState.Error(
                "Impossible de charger les cours."
            ),
            favorites = emptySet(),
            favoritesOnly = false,
            onRefresh = {},
            onCryptoClick = {},
            onToggleFavorite = {},
            onToggleFavoritesFilter = {},
        )
    }
}
