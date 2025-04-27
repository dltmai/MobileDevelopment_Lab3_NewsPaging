package com.tuyetmai.newspaging


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class NewsViewModel : ViewModel() {

    val news = Pager(PagingConfig(pageSize = 20)) {
        NewsPagingSource(RetrofitInstance.api, "13b5ed90dd674dbc9c63f93697c8bac2")
    }.flow.cachedIn(viewModelScope)
}