package com.tuyetmai.newspaging


import androidx.paging.PagingSource
import androidx.paging.PagingState

class NewsPagingSource(
    private val apiService: NewsApiService,
    private val apiKey: String
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getTopHeadlines(
                country = "us",
                page = page,
                pageSize = 20,
                apiKey = apiKey
            )
            LoadResult.Page(
                data = response.articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}