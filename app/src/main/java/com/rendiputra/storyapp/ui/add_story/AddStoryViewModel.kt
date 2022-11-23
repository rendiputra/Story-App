package com.rendiputra.storyapp.ui.add_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendiputra.storyapp.data.StoryRepository
import com.rendiputra.storyapp.domain.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    private val _uploadNewStoryState = MutableLiveData<Response<String>>()
    val uploadNewStoryState: LiveData<Response<String>>
        get() = _uploadNewStoryState

    fun uploadNewStory(token: String, image: File, description: String) {
        viewModelScope.launch {
            storyRepository.addNewStory(token, image, description).collect { response ->
                _uploadNewStoryState.postValue(response)
            }
        }
    }
}