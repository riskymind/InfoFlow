package com.asterisk.infoflow.ui.breaking_news

import androidx.lifecycle.ViewModel
import com.asterisk.infoflow.data.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
): ViewModel() {


}