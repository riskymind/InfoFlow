package com.asterisk.infoflow.ui.saved_news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asterisk.infoflow.R
import com.asterisk.infoflow.commons.NewsArticleListAdapter
import com.asterisk.infoflow.databinding.FragmentSaveNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveNewsFragment : Fragment(R.layout.fragment_save_news) {

    private var _binding: FragmentSaveNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SaveNewsViewModel>()
    private lateinit var savedNewsAdapter: NewsArticleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedNewsAdapter = NewsArticleListAdapter(
            onItemClick = { article ->
                val uri = Uri.parse(article.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireActivity().startActivity(intent)
            },
            onSaveClick = { article ->
                viewModel.saveArticle(article)
            }
        )

        setUpRecyclerView()
        observeSavedArticles()
    }

    private fun observeSavedArticles() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.savedArticles.collect {
                val result = it ?: return@collect
                binding.apply {
                    rvSavedNews.isVisible = result.isNotEmpty()
                    tvNoResult.isVisible = result.isEmpty()
                }
                savedNewsAdapter.submitList(result)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvSavedNews.apply {
            adapter = savedNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//            itemAnimator?.changeDuration = 0
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSavedNews.adapter = null
        _binding = null
    }
}
