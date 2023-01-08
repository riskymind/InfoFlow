package com.asterisk.infoflow.commons

import androidx.recyclerview.widget.RecyclerView
import com.asterisk.infoflow.R
import com.asterisk.infoflow.databinding.NewsItemBinding
import com.asterisk.infoflow.domain.model.NewsArticle
import com.bumptech.glide.Glide

class NewsArticleViewHolder(
    private val binding: NewsItemBinding,
    private val onItemClick: (Int) -> Unit,
    private val onSaveClick: (Int) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }

            imageView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSaveClick(position)
                }
            }
        }
    }

    fun bind(article: NewsArticle) {
        binding.apply {
            Glide.with(itemView)
                .load(article.urlToImage)
                .error(R.drawable.image_placeholder)
                .into(ivNewsImage)

            tvTitle.text = article.title ?: ""
            ivSaveImage.setImageResource(
                when {
                    article.isSaved -> R.drawable.ic_save_selected
                    else -> R.drawable.ic_save_unselected
                }
            )
        }
    }
}