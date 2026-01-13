package com.example.quotevault.ui.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.repository.CollectionRepository
import com.example.quotevault.domain.model.Collection
import com.example.quotevault.domain.model.CollectionWithQuotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionsUiState(
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false
)

data class CollectionDetailUiState(
    val collectionWithQuotes: CollectionWithQuotes? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(CollectionDetailUiState())
    val detailUiState: StateFlow<CollectionDetailUiState> = _detailUiState.asStateFlow()

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            collectionRepository.getCollections().fold(
                onSuccess = { collections ->
                    _uiState.value = _uiState.value.copy(
                        collections = collections,
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

    fun loadCollectionDetail(collectionId: String) {
        viewModelScope.launch {
            _detailUiState.value = _detailUiState.value.copy(isLoading = true)

            collectionRepository.getCollectionWithQuotes(collectionId).fold(
                onSuccess = { collectionWithQuotes ->
                    _detailUiState.value = _detailUiState.value.copy(
                        collectionWithQuotes = collectionWithQuotes,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _detailUiState.value = _detailUiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun createCollection(name: String, description: String?) {
        viewModelScope.launch {
            collectionRepository.createCollection(name, description).fold(
                onSuccess = {
                    loadCollections()
                    _uiState.value = _uiState.value.copy(showCreateDialog = false)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    // NEW: Create collection and add quote in one go
    fun createCollectionAndAddQuote(name: String, description: String?, quoteId: String) {
        viewModelScope.launch {
            collectionRepository.createCollection(name, description).fold(
                onSuccess = { collection ->
                    // Add quote to the newly created collection
                    collectionRepository.addQuoteToCollection(collection.id, quoteId)
                    loadCollections()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    // NEW: Add quote to existing collection
    suspend fun addQuoteToCollection(collectionId: String, quoteId: String) {
        collectionRepository.addQuoteToCollection(collectionId, quoteId).fold(
            onSuccess = {
                loadCollections()
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(error = error.message)
            }
        )
    }


    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collectionId).fold(
                onSuccess = {
                    loadCollections()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun removeQuoteFromCollection(collectionId: String, quoteId: String) {
        viewModelScope.launch {
            collectionRepository.removeQuoteFromCollection(collectionId, quoteId).fold(
                onSuccess = {
                    loadCollectionDetail(collectionId)
                },
                onFailure = { error ->
                    _detailUiState.value = _detailUiState.value.copy(error = error.message)
                }
            )
        }
    }

    fun showCreateDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateDialog = show)
    }
}
