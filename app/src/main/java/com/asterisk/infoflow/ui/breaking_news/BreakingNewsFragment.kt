package com.asterisk.infoflow.ui.breaking_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asterisk.infoflow.R
import com.asterisk.infoflow.commons.NewsArticleListAdapter
import com.asterisk.infoflow.commons.collectLatestLifeCycleFlow
import com.asterisk.infoflow.databinding.FragmentBreakingNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<BreakingNewsViewModel>()
    private lateinit var breakingNewsAdapter: NewsArticleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        breakingNewsAdapter = NewsArticleListAdapter()

        setupRecyclerview()

        observeBreakingNews()
    }

    private fun observeBreakingNews() {
        collectLatestLifeCycleFlow(viewModel.uiState) { newsState ->
            if (newsState.isLoading) {
                binding.swipeRefreshLayout.isRefreshing = true
            }

            if (newsState.isError.isNotEmpty()) {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.tvError.isVisible = true
                binding.tvError.text = newsState.isError
            }

            if (newsState.breakingNews.isNotEmpty()) {
                binding.swipeRefreshLayout.isRefreshing = false
                breakingNewsAdapter.submitList(newsState.breakingNews)
            }
        }
    }

    private fun setupRecyclerview() {
        binding.rvBreakingNews.apply {
            adapter = breakingNewsAdapter
            setHasFixedSize(true)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}