package com.smarttoolfactory.tutorial2_1unit_testing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var result by mutableStateOf<ResponseResult>(ResponseResult.Idle)

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> get() = _message

    fun loadMessage() {
        viewModelScope.launch {
            _message.value = "Greetings!"
        }
    }

    fun loadResultAsync() {
        viewModelScope.launch {
            result = ResponseResult.Loading
            delay(100)
            result = ResponseResult.Success("Result")
        }
    }
}

sealed class ResponseResult {
    object Idle : ResponseResult()
    object Loading : ResponseResult()
    data class Success(val data: String) : ResponseResult()
}