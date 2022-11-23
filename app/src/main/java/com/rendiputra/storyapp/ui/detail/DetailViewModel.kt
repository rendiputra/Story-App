package com.rendiputra.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendiputra.storyapp.data.StoryRepository
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.domain.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Response<Story>>()
    val story: LiveData<Response<Story>>
        get() = _story

    fun getDetailStory(token: String, id: String) {
        viewModelScope.launch {
            storyRepository.getDetailStory(token, id).collect { response ->
                _story.postValue(response)
            }
        }
    }
}