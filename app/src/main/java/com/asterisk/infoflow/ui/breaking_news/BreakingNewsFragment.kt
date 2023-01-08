package com.asterisk.infoflow.ui.breaking_news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asterisk.infoflow.R
import com.asterisk.infoflow.commons.NewsArticleListAdapter
import com.asterisk.infoflow.commons.Resource
import com.asterisk.infoflow.commons.collectLatestLifeCycleFlow
import com.asterisk.infoflow.commons.showSnackBar
import com.asterisk.infoflow.databinding.FragmentBreakingNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news), MenuProvider {

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

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        breakingNewsAdapter = NewsArticleListAdapter(
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

        observeBreakingNews()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onManualRefresh()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.onManualRefresh()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is EVent.ShowErrorMessage -> {
                        showSnackBar(
                            getString(R.string.could_not_refresh, event.error.localizedMessage)
                        )
                    }
                }
            }
        }
    }

    private fun observeBreakingNews() {
        collectLatestLifeCycleFlow(viewModel.breakingNews) {
            val result = it ?: return@collectLatestLifeCycleFlow
            binding.apply {
                swipeRefreshLayout.isRefreshing = result is Resource.Loading
                rvBreakingNews.isVisible = !result.data.isNullOrEmpty()
                tvError.isVisible = result.error != null && result.data.isNullOrEmpty()
                btnRetry.isVisible = result.error != null && result.data.isNullOrEmpty()
                tvError.text = getString(R.string.could_not_refresh, result.error)
            }

            breakingNewsAdapter.submitList(result.data) {
                if (viewModel.scrollToTop) {
                    binding.rvBreakingNews.scrollToPosition(0)
                    viewModel.scrollToTop = false
                }
            }
        }
    }


    private fun setupRecyclerview() {
        binding.rvBreakingNews.apply {
            adapter = breakingNewsAdapter
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        activity?.removeMenuProvider(this)
        binding.rvBreakingNews.adapter = null
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_breaking_news, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.refresh -> {
                viewModel.onManualRefresh()
                true
            }
            else -> false
        }
    }


}