package com.example.florasync.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.florasync.model.dto.PlantDto

class PlantPagingSource(
    private val service: FloraSyncApiService,
    private val query: String
) : PagingSource<Int, PlantDto>() {

    override fun getRefreshKey(state: PagingState<Int, PlantDto>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlantDto> = try {
        val page = params.key ?: 1
        val response = service.getPlantsPaged(page = page, pageSize = 30)

        val filtered = if (query.isBlank()) {
            response.items
        } else {
            response.items.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true)
            }
        }

        LoadResult.Page(
            data = filtered,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (filtered.isEmpty()) null else page + 1
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}

