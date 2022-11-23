package com.rendiputra.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendiputra.storyapp.data.StoryRepository
import com.rendiputra.storyapp.data.datastore.AuthPreferences
import com.rendiputra.storyapp.domain.Login
import com.rendiputra.storyapp.domain.Register
import com.rendiputra.storyapp.domain.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _loginState = MutableLiveData<Response<Login>>()
    val loginState: LiveData<Response<Login>>
        get() = _loginState

    private val _registerState = MutableLiveData<Response<Register>>()
    val registerState: LiveData<Response<Register>>
        get() = _registerState

    private val _authToken = MutableLiveData<String>()
    val authToken: LiveData<String>
        get() = _authToken

    init {
        viewModelScope.launch {
            authPreferences.authToken.collect { token ->
                _authToken.value = token
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            storyRepository.login(email, password).collect { response ->
                _loginState.postValue(response)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            storyRepository.register(name, email, password).collect { response ->
                _registerState.postValue(response)
            }
        }
    }

    fun updateAuthToken(token: String) {
        viewModelScope.launch {
            authPreferences.updateAuthToken(token)
        }
    }

    fun removeAuthToken() {
        viewModelScope.launch {
            authPreferences.removeAuthToken()
        }
    }
}