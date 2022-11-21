package com.asterisk.infoflow.ui.breaking_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.data.repository.NewsRepositoryImpl
import com.asterisk.infoflow.domain.use_cases.BreakingNewsUseCase
import com.asterisk.infoflow.ui.breaking_news.uistate.BreakingNewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val breakingNewsUseCase: BreakingNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreakingNewsUiState())
    val uiState: StateFlow<BreakingNewsUiState> = _uiState.asStateFlow()

    init {
        getBreakingNews()
    }

    private fun getBreakingNews() {
        viewModelScope.launch {
            breakingNewsUseCase().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = result.error ?: "unexpected error!!"
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            breakingNews = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
            }.launchIn(this)
        }

    }

}