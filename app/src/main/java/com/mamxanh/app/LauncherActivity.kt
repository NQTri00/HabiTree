package com.mamxanh.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/** Aliases target this short-lived entry, so icon replacement never targets the UI activity. */
class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launch = Intent(intent).setClass(this, MainActivity::class.java)
        launch.addFlags(
            if (isTaskRoot) {
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            } else {
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
        )
        startActivity(launch)
        finish()
    }
}
