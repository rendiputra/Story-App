package com.rendiputra.storyapp.data

import com.rendiputra.storyapp.data.network.response.asDomain
import com.rendiputra.storyapp.data.network.service.StoryService
import com.rendiputra.storyapp.domain.Login
import com.rendiputra.storyapp.domain.Register
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.domain.Story
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor (private val storyService: StoryService) {

    fun login(email: String, password: String): Flow<Response<Login>> = flow {
        emit(Response.Loading)
        when (val response = storyService.login(email, password)) {
            is NetworkResponse.Success -> {
                if (response.body.loginResult != null) {
                    emit(Response.Success(response.body.loginResult!!.asDomain()))
                }
            }
            is NetworkResponse.Error -> {
                emit(Response.Error(response.body?.message))
            }
        }
    }

    fun register(name: String, email: String, password: String): Flow<Response<Register>> = flow {
        emit(Response.Loading)
        when (val response = storyService.register(name, email, password)) {
            is NetworkResponse.Success -> {
                emit(Response.Success(response.body.asDomain()))
            }
            is NetworkResponse.Error -> {
                emit(Response.Error(response.body?.message))
            }
        }
    }

    fun getStories(token: String): Flow<Response<List<Story>>> = flow {
        emit(Response.Loading)
        when (val response = storyService.getStories(token)) {
            is NetworkResponse.Success -> {
                if (response.body.listStory != null) {
                    emit(Response.Success(response.body.listStory!!.asDomain()))
                } else {
                    emit(Response.Empty)
                }
            }
            is NetworkResponse.Error -> {
                emit(Response.Error(response.body?.message))
            }
        }
    }

    fun getDetailStory(token: String, id: String): Flow<Response<Story>> = flow {
        emit(Response.Loading)
        when (val response = storyService.getDetailStory(token, id)) {
            is NetworkResponse.Success -> {
                if (response.body.story != null) {
                    emit(Response.Success(response.body.story!!.asDomain()))
                } else {
                    emit(Response.Empty)
                }

            }
            is NetworkResponse.Error -> {
                Response.Error(response.body?.message)
            }
        }
    }

    fun addNewStory(token: String, image: File, description: String): Flow<Response<String>> = flow {
        emit(Response.Loading)
        try {
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                imageRequestBody
            )

            when (val response = storyService.addNewStory(token, imageMultiPart, descriptionRequestBody)) {
                is NetworkResponse.Success -> {
                    emit(Response.Success(response.body.message))
                }
                is NetworkResponse.Error -> {
                    emit(Response.Error(response.body?.message))
                }
            }
        } catch (exception: Exception) {
            emit(Response.Error(exception.message))
        }
    }
}