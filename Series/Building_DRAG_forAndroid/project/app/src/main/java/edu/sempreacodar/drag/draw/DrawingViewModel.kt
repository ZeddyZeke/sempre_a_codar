package edu.sempreacodar.drag.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.sempreacodar.drag.draw.model.GameTimer
import edu.sempreacodar.drag.draw.model.currentTimeSeconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Suspending function that delays execution for the specified [time] while publishing the remaining
 * time to [publishTo]. The function returns once the specified time elapses.
 */
suspend fun countDownFrom(time: GameTimer, publishTo: MutableLiveData<GameTimer>) {
    val startTimeInSeconds = currentTimeSeconds()
    val endTimeInSeconds = startTimeInSeconds + time.toSeconds()

    while(currentTimeSeconds() < endTimeInSeconds) {
        delay(250)
        publishTo.value = GameTimer.fromSeconds((endTimeInSeconds - currentTimeSeconds()).toInt())
    }
}

/**
 * The view model for the drawing screen.
 */
class DrawingViewModel : ViewModel() {

    enum class State {
        INITIALIZED, DRAWING, TIME_EXPIRED
    }

    /**
     * The current timer value
     */
    val timerValue: LiveData<GameTimer> = MutableLiveData()
    private val _timerValue = timerValue as MutableLiveData<GameTimer>

    /**
     * The current screen state
     */
    val state: LiveData<State> = MutableLiveData(State.INITIALIZED)
    private val _state = state as MutableLiveData<State>

    /**
     * Starts the game timer if the screen is in it's initial state
     * @param theTimerValue the timer's initial value
     * @return the [Job] instance corresponding to the count down timer that was started, or null
     * if the timer was not started
     */
    fun maybeStartTimer(theTimerValue: GameTimer): Job? {

        if (state.value != State.INITIALIZED)
            return null

        return viewModelScope.launch {
            _state.value = State.DRAWING
            _timerValue.value = theTimerValue
            countDownFrom(theTimerValue, _timerValue)
            _state.value = State.TIME_EXPIRED
        }
    }

    // TODO: Extend view model with drawing operations
}