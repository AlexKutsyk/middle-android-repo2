package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->

                    val currentMessages = _messages.value
                    _messages.value =
                        currentMessages + Message.OtherMessage(response)
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _messages.update { messageList -> messageList + Message.MyMessage(messageText) }
    }
}