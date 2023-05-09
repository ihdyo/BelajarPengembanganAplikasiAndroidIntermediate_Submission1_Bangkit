package com.ihdyo.postit.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ihdyo.postit.MainActivity
import com.ihdyo.postit.R
import com.ihdyo.postit.utils.SessionManager
import com.ihdyo.postit.utils.UiConstValue

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)
        val isLogin = sessionManager.isLogin
        val targetActivity = if (isLogin) MainActivity::class.java else LoginActivity::class.java

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, targetActivity))
            finish()
        }, UiConstValue.LOADING_TIME)
    }
}