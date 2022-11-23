package com.rendiputra.storyapp.data.network.service

import com.rendiputra.storyapp.data.network.response.*
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoryService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : NetworkResponse<LoginResponse, CommonNetworkErrorResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : NetworkResponse<RegisterResponse, CommonNetworkErrorResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String
    ) : NetworkResponse<StoryResponse, CommonNetworkErrorResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : NetworkResponse<DetailStoryResponse, CommonNetworkErrorResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : NetworkResponse<AddNewStoryResponse, CommonNetworkErrorResponse>
}