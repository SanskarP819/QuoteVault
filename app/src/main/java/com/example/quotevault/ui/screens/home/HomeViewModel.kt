package com.example.quotevault.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.repository.FavoriteRepository
import com.example.quotevault.data.repository.QuoteRepository
import com.example.quotevault.domain.model.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val quoteOfTheDay: Quote? = null,
    val recentQuotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("SUPABASE_TEST", "HomeViewModel INIT")
        loadData()
    }

    fun loadData() {

        Log.d("SUPABASE_TEST", "loadData called")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load quote of the day
            quoteRepository.getRandomQuote().fold(
                onSuccess = { quote ->
                    Log.d("SUPABASE_TEST", "Quote of the day: $quote")
                    _uiState.value = _uiState.value.copy(quoteOfTheDay = quote)
                },
                onFailure = {
                    Log.d("SUPABASE_TEST", "Error loading quote of the day",it)
                }
            )

            // Load recent quotes
            quoteRepository.getQuotes(page = 0, pageSize = 10).fold(
                onSuccess = { quotes ->
                    _uiState.value = _uiState.value.copy(
                        recentQuotes = quotes,
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

            // Update UI
            _uiState.value = _uiState.value.copy(
                quoteOfTheDay = if (_uiState.value.quoteOfTheDay?.id == quote.id) {
                    _uiState.value.quoteOfTheDay?.copy(isFavorite = !quote.isFavorite)
                } else {
                    _uiState.value.quoteOfTheDay
                },
                recentQuotes = _uiState.value.recentQuotes.map {
                    if (it.id == quote.id) it.copy(isFavorite = !quote.isFavorite) else it
                }
            )
        }
    }
}