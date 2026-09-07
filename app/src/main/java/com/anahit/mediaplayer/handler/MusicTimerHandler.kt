package com.anahit.mediaplayer.handler

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class MusicTimerHandler {

    private lateinit var timerJob: Job
    private val timerLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val typeOfNextMusic: MutableLiveData<Unit> = MutableLiveData()
    private val currentMusicTime: MutableLiveData<Int> = MutableLiveData()

    fun updateMusicTime(musicTime: Int){
        currentMusicTime.value = musicTime
    }
    fun sendTypeOfNextMusicAction(){
        typeOfNextMusic.value = Unit
    }

    fun stopTimerJob(){
        if (this::timerJob.isInitialized){
            timerJob.cancelChildren()
        }
    }

    fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.IO).launch {
            handleTimer()
        }
    }
    private suspend fun handleTimer() {
        delay(1000)
        timerLiveData.postValue(Unit)
        startTimer()
    }

    /**
     *  Live data getters
     */
    fun timerLiveData() = timerLiveData
    fun typeOfNextMusicLiveData() = typeOfNextMusic
    fun currentMusicTimeLiveData() = currentMusicTime
}