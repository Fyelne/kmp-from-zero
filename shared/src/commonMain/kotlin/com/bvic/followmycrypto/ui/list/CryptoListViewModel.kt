
package com.bvic.followmycrypto.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.data.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CryptoListViewModel(
    private val repository: CryptoRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _sourceUiState = MutableStateFlow<CryptoListUiState>(CryptoListUiState.Loading)

    private val _favoritesOnly = MutableStateFlow(false)

    val favoritesOnly: StateFlow<Boolean> =
        _favoritesOnly.asStateFlow()

    val favorites: StateFlow<Set<String>> =
        favoriteRepository.favorites

    val uiState: StateFlow<CryptoListUiState> =
        combine(
            _sourceUiState,
            favoriteRepository.favorites,
            _favoritesOnly,
        ) { state, favoriteSymbols, onlyFavorites ->

            if (state is CryptoListUiState.Success && onlyFavorites) {
                state.copy(
                    cryptos = state.cryptos.filter {
                        it.symbol in favoriteSymbols
                    }
                )
            } else {
                state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CryptoListUiState.Loading,
        )

    init {
        load()
    }

    fun refresh() = load()

    fun toggleFavorite(symbol: String) {
        //log
        
        favoriteRepository.toggleFavorite(symbol)
    }

    fun toggleFavoritesFilter() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    private fun load() {
        _sourceUiState.value = CryptoListUiState.Loading
        viewModelScope.launch {
            try {
                _sourceUiState.value = CryptoListUiState.Success(repository.getCryptos())
            } catch (e: Exception) {
                _sourceUiState.value = CryptoListUiState.Error("Impossible de charger les cours.")
            }
        }
    }
}
