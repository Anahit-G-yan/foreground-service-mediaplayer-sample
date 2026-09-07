package com.anahit.mediaplayer.view.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseActivity: AppCompatActivity() {

    fun openFragment(container: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(container, fragment, fragment::class.java.name)
            .addToBackStack(fragment::class.java.name)
            .commit()
    }
}