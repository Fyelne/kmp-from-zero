
package com.bvic.followmycrypto.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoriteRepository {

    private val _favorites =
        MutableStateFlow<Set<String>>(emptySet())

    val favorites: StateFlow<Set<String>> =
        _favorites.asStateFlow()

    fun toggleFavorite(symbol: String) {
        _favorites.update { current ->
            if (symbol in current) {
                current - symbol
            } else {
                current + symbol
            }
        }
    }
}
