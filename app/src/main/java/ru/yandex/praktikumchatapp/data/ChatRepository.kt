package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                var currentDelay = DELAY_FACTOR
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                true
            }
    }

    companion object {
        const val DELAY_FACTOR = 500L
    }
}