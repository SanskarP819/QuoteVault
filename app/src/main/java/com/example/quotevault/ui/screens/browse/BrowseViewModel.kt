package com.example.quotevault.ui.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.repository.FavoriteRepository
import com.example.quotevault.data.repository.QuoteRepository
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    val quotes: List<Quote> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    init {
        loadQuotes()
    }

    fun loadQuotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val category = if (_uiState.value.selectedCategory == "All") null
            else _uiState.value.selectedCategory

            quoteRepository.getQuotes(category = category).fold(
                onSuccess = { quotes ->
                    _uiState.value = _uiState.value.copy(
                        quotes = quotes,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadQuotes()
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            loadQuotes()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            quoteRepository.searchQuotes(query).fold(
                onSuccess = { quotes ->
                    _uiState.value = _uiState.value.copy(
                        quotes = quotes,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            if (quote.isFavorite) {
                favoriteRepository.removeFavorite(quote.id)
            } else {
                favoriteRepository.addFavorite(quote.id)
            }

            _uiState.value = _uiState.value.copy(
                quotes = _uiState.value.quotes.map {
                    if (it.id == quote.id) it.copy(isFavorite = !quote.isFavorite) else it
                }
            )
        }
    }
}