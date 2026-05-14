package com.kabaddiarena

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MatchEvent(
    val team: String,
    val action: String,
    val timeRemaining: Int,
    val points: Int
)

class MatchViewModel : ViewModel() {

    // Match States
    var raidAttempts by mutableIntStateOf(0)
    var raidSuccess by mutableIntStateOf(0)
    var tacklePoints by mutableIntStateOf(0)
    var teamAScore by mutableIntStateOf(0)
    var teamBScore by mutableIntStateOf(0)
    
    // Timer States
    var timeLeft by mutableIntStateOf(30)
    var isRunning by mutableStateOf(false)
    
    // Timeline Data
    private val _timeline = mutableStateListOf<MatchEvent>()
    val timeline: List<MatchEvent> = _timeline

    private var timerJob: Job? = null

    fun startMatch() {
        if (isRunning) return
        isRunning = true
        timerJob = viewModelScope.launch {
            while (timeLeft > 0 && isRunning) {
                delay(1000)
                timeLeft--
            }
            isRunning = false
        }
    }

    fun pauseMatch() {
        isRunning = false
        timerJob?.cancel()
    }

    fun resetMatch() {
        pauseMatch()
        timeLeft = 30
        raidAttempts = 0
        raidSuccess = 0
        tacklePoints = 0
        teamAScore = 0
        teamBScore = 0
        _timeline.clear()
    }

    fun onRaidSuccess() {
        raidAttempts++
        raidSuccess++
        teamAScore++
        _timeline.add(0, MatchEvent("TEAM A", "Raid Success", timeLeft, 1))
    }

    fun onRaidFail() {
        raidAttempts++
        _timeline.add(0, MatchEvent("TEAM A", "Raid Fail", timeLeft, 0))
    }

    fun onTackle() {
        tacklePoints++
        teamBScore++
        _timeline.add(0, MatchEvent("TEAM B", "Successful Tackle", timeLeft, 1))
    }
}
