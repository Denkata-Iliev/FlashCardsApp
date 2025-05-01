package com.example.flashcardsapp.ui.study

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.data.repository.CardRepository
import com.example.flashcardsapp.ui.settings.SettingsDefaults
import com.example.flashcardsapp.ui.settings.SettingsKeys
import com.example.flashcardsapp.ui.settings.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimedStudyViewModel(
    private val applicationContext: Context,
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _timedUiState = mutableStateOf(TimedUiState())
    val timedUiState: State<TimedUiState> = _timedUiState

    private lateinit var countDownTimer: CountDownTimer

    private var _initialSeconds = 0L

    var progress = mutableFloatStateOf(1f)
        private set

    var seconds = mutableLongStateOf(0)
        private set

    init {
        viewModelScope.launch {
            val limit = applicationContext.dataStore.data.map {
                it[SettingsKeys.timedLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
            }.first()

            _initialSeconds = applicationContext.dataStore.data.map {
                it[SettingsKeys.timerSecondsKey] ?: SettingsDefaults.DEFAULT_TIMER_SECONDS
            }.first().toLong()

            seconds.longValue = TimeUnit.SECONDS.toMillis(_initialSeconds)

            countDownTimer = getCountdownTimer(seconds.longValue)

            _timedUiState.value = TimedUiState(
                cards = cardRepository.getRandomCards(limit = limit, deckId = deckId)
            )
        }
    }

    fun startTimer() {
        countDownTimer.start()
    }

    fun resetTimer() {
        progress.floatValue = 1f
        countDownTimer.cancel()
        seconds.longValue = TimeUnit.SECONDS.toMillis(_initialSeconds)
    }

    fun removeCardFromSession(card: Card) {
        val temp = _timedUiState.value.cards.toMutableList()
        temp.remove(card)
        _timedUiState.value = TimedUiState(temp.toList())
    }

    private fun getCountdownTimer(millisInFuture: Long): CountDownTimer =
        object : CountDownTimer(millisInFuture, 50L) {
            override fun onTick(timeLeft: Long) {
                seconds.longValue = timeLeft
                progress.floatValue = timeLeft.toFloat() / (_initialSeconds * 1000f)
            }

            override fun onFinish() {
                progress.floatValue = 0f
            }
        }
}

data class TimedUiState(val cards: List<Card> = listOf())