package com.example.flashcardsapp.ui.study

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimedStudyViewModel(
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _timedUiState = mutableStateOf(TimedUiState())
    val timedUiState: State<TimedUiState> = _timedUiState

    init {
        viewModelScope.launch {
            _timedUiState.value = TimedUiState(
                cards = cardRepository.getRandomCards(limit = 10, deckId = deckId)
            )
        }
    }

    var progress = mutableFloatStateOf(1f)
        private set

    var seconds = mutableLongStateOf(TimeUnit.SECONDS.toMillis(10))
        private set

    private val countDownTimer: CountDownTimer = object : CountDownTimer(seconds.longValue, 50L) {
        override fun onTick(timeLeft: Long) {
            seconds.longValue = timeLeft
            progress.floatValue = timeLeft.toFloat() / 10_000f
        }

        override fun onFinish() {
            progress.floatValue = 0f
        }
    }

    fun startTimer() {
        countDownTimer.start()
    }

    fun resetTimer() {
        progress.floatValue = 1f
        countDownTimer.cancel()
        seconds.longValue = TimeUnit.SECONDS.toMillis(10)
    }

    fun removeCardFromSession(card: Card) {
        val temp = _timedUiState.value.cards.toMutableList()
        temp.remove(card)
        _timedUiState.value = TimedUiState(temp.toList())
    }
}

data class TimedUiState(val cards: List<Card> = listOf())