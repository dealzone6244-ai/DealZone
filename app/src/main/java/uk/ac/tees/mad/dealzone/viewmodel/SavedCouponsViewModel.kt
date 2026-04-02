package uk.ac.tees.mad.dealzone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.Model.ResultState
import uk.ac.tees.mad.dealzone.data.AuthRepository
import uk.ac.tees.mad.dealzone.domain.Repo.Repo

data class SavedCouponsUiState(
    val isLoading: Boolean = false,
    val savedCoupons: List<Product> = emptyList(),
    val errorMessage: String? = null
)

class SavedCouponsViewModel(
    private val repo: Repo,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedCouponsUiState())
    val uiState: StateFlow<SavedCouponsUiState> = _uiState.asStateFlow()

    init {
        getSavedCoupons()
    }

    fun getSavedCoupons() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            repo.getSavedCoupons(uid).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is ResultState.Succes -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            savedCoupons = result.data,
                            errorMessage = null
                        )
                    }
                    is ResultState.error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun removeCoupon(productId: Int) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = repo.removeCoupon(uid, productId)
            if (result is ResultState.Succes) {
                getSavedCoupons()
            }
        }
    }

    class Factory(private val repo: Repo, private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SavedCouponsViewModel::class.java)) {
                return SavedCouponsViewModel(repo, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
