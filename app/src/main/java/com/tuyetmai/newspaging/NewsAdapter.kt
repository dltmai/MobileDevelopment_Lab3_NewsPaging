package com.tuyetmai.newspaging

import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private val sentimentAnalyzer: SentimentAnalyzer) :
    PagingDataAdapter<Article, NewsAdapter.NewsViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)

        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        article?.let { holder.bind(it, sentimentAnalyzer) }
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.text_title)
        private val description = itemView.findViewById<TextView>(R.id.text_description)
        private val image = itemView.findViewById<ImageView>(R.id.imageView)
        private val sentiment = itemView.findViewById<TextView>(R.id.text_sentiment)

        fun bind(article: Article, analyzer: SentimentAnalyzer) {
            title.text = article.title
            description.text = article.description
            Glide.with(itemView).load(article.urlToImage).into(image)

            // Phân tích cảm xúc
            val sentimentResult = analyzer.analyze(article.description ?: "")

            // Hiển thị cảm xúc
            sentiment.text = "Sentiment: $sentimentResult"

            // Đổi màu chữ theo cảm xúc (optional)
            when (sentimentResult) {
                "Positive" -> sentiment.setTextColor(android.graphics.Color.GREEN)
                "Neutral" -> sentiment.setTextColor(android.graphics.Color.GRAY)
                "Negative" -> sentiment.setTextColor(android.graphics.Color.RED)
                else -> sentiment.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Article, newItem: Article) =
                oldItem == newItem
        }
    }
}