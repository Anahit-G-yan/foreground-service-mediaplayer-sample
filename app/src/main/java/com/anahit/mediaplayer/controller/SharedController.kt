package com.anahit.mediaplayer.controller

import android.content.Context

class SharedController(private val context: Context) {

    private val shared = context.getSharedPreferences("type_of_service", Context.MODE_PRIVATE)

    fun saveForegroundService(type: String) {
        shared.edit().putString("typeOfService", type).apply()
    }


    fun checkService(): Boolean {
        val activeService = shared.getString("typeOfService", "")?:""
        return activeService.isNotEmpty()
    }

    fun removeForegroundService(){
        shared.edit().clear().commit()
    }
}