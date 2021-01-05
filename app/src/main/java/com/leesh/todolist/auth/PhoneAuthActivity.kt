package com.leesh.todolist.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leesh.todolist.R
import com.leesh.todolist.databinding.ActivityHomeBinding
import com.leesh.todolist.databinding.ActivityPhoneAuthBinding
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneAuthBinding


    private lateinit var auth: FirebaseAuth


    private val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuth: PhoneAuthCredential) {
                Toast.makeText(applicationContext, "인증코드가 전송되었습니다. 60초 이내에 입력해주세요 :)", Toast.LENGTH_SHORT).show()

                val authNum =  phoneAuth.smsCode.toString()

                binding.etEnterCode.isEnabled = true
                binding.tvAuthNext.isEnabled = true
                binding.etPhone.isEnabled = true

                Log.e("입력해야할 인증번호 = ", authNum)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, "인증실패", Toast.LENGTH_SHORT).show()
                binding.etPhone.isEnabled = true
                Log.e("PhoneAuthActivity", p0.toString())
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e("PhoneAuthActivity", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token

            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Firebase Auth 초기화
        auth = Firebase.auth

        binding.tvAuthRequest.setOnClickListener{
            val phoneNumber = "+82" + binding.etPhone.text.toString() // 폰 번호 획득

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options) // 옵션 할당
        }

    }// onCreate 끝


}