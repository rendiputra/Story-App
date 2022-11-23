package com.rendiputra.storyapp.ui.home

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
class MainViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<Response<List<Story>>>()
    val stories: LiveData<Response<List<Story>>>
        get() = _stories

    fun getStories(token: String) {
        viewModelScope.launch {
            storyRepository.getStories(token).collect { response ->
                _stories.postValue(response)
            }
        }
    }
}