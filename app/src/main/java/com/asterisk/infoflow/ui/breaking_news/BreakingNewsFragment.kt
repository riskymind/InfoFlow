package com.asterisk.infoflow.ui.breaking_news

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asterisk.infoflow.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {
    private val viewModel by viewModels<BreakingNewsViewModel>()


}