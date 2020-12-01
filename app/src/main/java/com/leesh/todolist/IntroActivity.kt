package com.leesh.todolist

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat

class IntroActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }



//    public override fun onResume() {
//        super.onResume()  // Always call the superclass method first
//
//        val handler = Handler()
//        handler.postDelayed({
//            val intent = Intent(this, SelectLoginOrJoinActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
//            finish()
//        }, 3000)// 3 초
//
//    }

    // 화면 재사용시에 onCreate 대신에 호출됨.
    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }






}


