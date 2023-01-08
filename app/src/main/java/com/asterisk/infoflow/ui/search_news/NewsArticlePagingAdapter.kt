package com.asterisk.infoflow.ui.search_news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.asterisk.infoflow.commons.NewsArticleComparator
import com.asterisk.infoflow.commons.NewsArticleViewHolder
import com.asterisk.infoflow.databinding.NewsItemBinding
import com.asterisk.infoflow.domain.model.NewsArticle

class NewsArticlePagingAdapter(
    private val onItemClick: (NewsArticle) -> Unit,
    private val onSaveClick: (NewsArticle) -> Unit,
) :
    PagingDataAdapter<NewsArticle, NewsArticleViewHolder>(NewsArticleComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {
        val binding =
            NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsArticleViewHolder(
            binding,
            onItemClick = { position ->
                val article = getItem(position)
                if (article != null) {
                    onItemClick(article)
                }
            },
            onSaveClick = { position ->
                val article = getItem(position)
                if (article != null) {
                    onSaveClick(article)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}