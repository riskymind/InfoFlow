package com.asterisk.infoflow.ui.search_news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.asterisk.infoflow.R
import com.asterisk.infoflow.commons.*
import com.asterisk.infoflow.databinding.FragmentSearchNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news), MenuProvider {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchNewsAdapter: NewsArticlePagingAdapter
    private val viewModel by viewModels<SearchNewsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        binding.swipeRefreshLayout.isEnabled = false
        searchNewsAdapter = NewsArticlePagingAdapter(
            onItemClick = { article ->
                val uri = Uri.parse(article.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireActivity().startActivity(intent)
            },
            onSaveClick = { article ->
                viewModel.saveArticle(article)
            }
        )

        setupRecyclerview()

        collectLatestLifeCycleFlow(viewModel.searchResults) {
            binding.apply {
                tvInstruction.isVisible = false
                swipeRefreshLayout.isEnabled = true
            }
            searchNewsAdapter.submitData(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchNewsAdapter.loadStateFlow
                .distinctUntilChangedBy { it.source.refresh }
                .filter { it.source.refresh is LoadState.NotLoading }
                .collect {
                    if (viewModel.pendingScrollToTopAfterNewQuery) {
                        binding.rvSearchNews.scrollToPosition(0)
                        viewModel.pendingScrollToTopAfterNewQuery = false
                    }
                    if (viewModel.pendingScrollToTopAfterRefresh && it.mediator?.refresh is LoadState.NotLoading) {
                        binding.rvSearchNews.scrollToPosition(0)
                        viewModel.pendingScrollToTopAfterRefresh = false
                    }
                }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchNewsAdapter.loadStateFlow.collect { loadState ->
                when (val refresh = loadState.mediator?.refresh) {
                    LoadState.Loading -> {
                        binding.apply {
                            tvError.isVisible = false
                            btnRetry.isVisible = false
                            swipeRefreshLayout.isRefreshing = true
                            tvNoResult.isVisible = false
                            rvSearchNews.showIfOrInvisible {
                                !viewModel.newQueryInProgress && searchNewsAdapter.itemCount > 0
                            }
                        }
                        viewModel.refreshInProgress = true
                        viewModel.pendingScrollToTopAfterRefresh = true
                    }
                    is LoadState.NotLoading -> {
                        binding.apply {
                            tvError.isVisible = false
                            btnRetry.isVisible = false
                            swipeRefreshLayout.isRefreshing = false
                            rvSearchNews.isVisible = searchNewsAdapter.itemCount > 0

                            val noResult =
                                searchNewsAdapter.itemCount < 1 && loadState.append.endOfPaginationReached
                                        && loadState.source.append.endOfPaginationReached
                            tvNoResult.isVisible = noResult
                        }
                        viewModel.refreshInProgress = false
                        viewModel.newQueryInProgress = false

                    }
                    is LoadState.Error -> {
                        binding.apply {
                            tvNoResult.isVisible = false
                            swipeRefreshLayout.isRefreshing = false
                            rvSearchNews.isVisible = searchNewsAdapter.itemCount > 0
                            viewModel.newQueryInProgress = false

                            val noCachedResults =
                                searchNewsAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached
                            tvError.isVisible = noCachedResults
                            btnRetry.isVisible = noCachedResults

                            val errorMsg = getString(
                                R.string.could_not_load_search_results,
                                refresh.error.localizedMessage
                                    ?: getString(R.string.unknown_error_occurred)
                            )

                            tvError.text = errorMsg

                            if (viewModel.refreshInProgress) {
                                showSnackBar(errorMsg)
                            }
                            viewModel.refreshInProgress = false
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }
            binding.swipeRefreshLayout.setOnRefreshListener {
                searchNewsAdapter.refresh()
            }

            binding.btnRetry.setOnClickListener {
                searchNewsAdapter.refresh()
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSearchNews.adapter = null
        _binding = null
    }

    private fun setupRecyclerview() {
        binding.rvSearchNews.apply {
            adapter = searchNewsAdapter.withLoadStateFooter(
                NewsArticleLoadStateAdapter(searchNewsAdapter::retry)
            )
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.onSearchQuerySubmit("nigeria")
//    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.onQueryTextSubmit {
            viewModel.onSearchQuerySubmit(it)
            searchView.clearFocus()
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_refresh -> {
                searchNewsAdapter.refresh()
                true
            }
            else -> false
        }
    }
}