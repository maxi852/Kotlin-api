package com.example.appservice

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiService {
    @GET
    suspend fun getListaImagenes(@Url url: String): Response<DogResponse>

    @GET
    suspend fun getListOfBreed(@Url url: String): Response<BreedsResponse>
}