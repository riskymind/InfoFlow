package com.asterisk.infoflow.ui.breaking_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.data.repository.NewsRepositoryImpl
import com.asterisk.infoflow.domain.model.NewsArticle
import com.asterisk.infoflow.domain.repository.NewsRepository
import com.asterisk.infoflow.domain.use_cases.BreakingNewsUseCase
import com.asterisk.infoflow.domain.use_cases.DeleteOlderNonSavedArticleUseCase
import com.asterisk.infoflow.domain.use_cases.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val newsUseCase: BreakingNewsUseCase,
    private val deleteUseCase: DeleteOlderNonSavedArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) : ViewModel() {


    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    private val eventChannel = Channel<EVent>()
    val events = eventChannel.receiveAsFlow()

    var scrollToTop = false

    init {
        viewModelScope.launch {
            deleteUseCase()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val breakingNews = refreshTrigger.flatMapLatest { refresh ->
        newsUseCase(
            forceRefresh = refresh == Refresh.FORCE,
            onFetchSucceed = {
                scrollToTop = true
            },
            onFetchFailed = {
                viewModelScope.launch {
                    eventChannel.send(EVent.ShowErrorMessage(it))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun onManualRefresh() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }

    }

    fun onStart() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }

    }


    fun saveArticle(article: NewsArticle) {
        viewModelScope.launch {
            saveArticleUseCase(article)
        }
    }

}

enum class Refresh {
    FORCE, NORMAL
}

sealed class EVent {
    data class ShowErrorMessage(val error: Throwable) : EVent()
}