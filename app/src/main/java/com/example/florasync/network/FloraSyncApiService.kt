package com.example.florasync.network

import com.example.florasync.model.dto.PlantDto
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File

interface FloraSyncApiService {

    @GET("plants")
    suspend fun getPlants(): List<PlantDto>

    @GET("plants/{id}")
    suspend fun getPlantById(id: Long): PlantDto

    @GET("plants/image/{fileName}")
    suspend fun getPlantImageByFileName(fileName: String): File

    @GET("plants/paged")
    suspend fun getPlantsPaged(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResponse<PlantDto>


}