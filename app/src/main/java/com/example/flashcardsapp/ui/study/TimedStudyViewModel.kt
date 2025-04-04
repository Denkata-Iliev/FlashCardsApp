package com.example.flashcardsapp.ui.study

import android.os.CountDownTimer
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import com.example.flashcardsapp.data.repository.CardRepository
import java.util.concurrent.TimeUnit

class TimedStudyViewModel(
    private val deckId: Int,
    private val cardRepository: CardRepository
) : ViewModel() {
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

    fun stopTimer() {
        progress.floatValue = 1f
        countDownTimer.cancel()
        seconds.longValue = TimeUnit.SECONDS.toMillis(10)
    }
}