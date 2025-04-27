package com.tuyetmai.newspaging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.tuyetmai.newspaging.R


class MainActivity : AppCompatActivity() {
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var adapter: NewsAdapter
    private lateinit var sentimentAnalyzer: SentimentAnalyzer  // Declare SentimentAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the SentimentAnalyzer
        sentimentAnalyzer = SentimentAnalyzer(this)

        // Create adapter and pass the sentiment analyzer
        adapter = NewsAdapter(sentimentAnalyzer)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Collect news data and submit to adapter
        lifecycleScope.launch {
            viewModel.news.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sentimentAnalyzer.close()  // Close the sentiment analyzer when the activity is destroyed
    }
}