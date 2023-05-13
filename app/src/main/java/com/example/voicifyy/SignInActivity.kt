package com.example.voicifyy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.voicifyy.databinding.ActivitySignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser:FirebaseUser?=null
    private var phoneNumber:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        currentUser=auth.currentUser
        setContentView(binding.root)
        if(auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.buttonSignIn.setOnClickListener{
            if(binding.number.text.isEmpty())
            {
                Toast.makeText(this, "enter number", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("number", binding.number.text!!.toString())
                startActivity(intent)
            }
        }
        val buttonClick = findViewById<Button>(R.id.SignUp)
        buttonClick.setOnClickListener {
            val intent = Intent(this, SignUp2::class.java)
            startActivity(intent)
        }
//        val buttonClick2 = findViewById<Button>(R.id.buttonSignIn)
//        buttonClick2.setOnClickListener {
//            generate_OTP()
//        }
    }
    fun send_home(){
        val loginIntent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
    override fun onStart(){
        super.onStart()
        if(currentUser!=null){
            send_home()
            finish()
        }
    }
    fun generate_OTP(view: View) {
        phoneNumber = binding.number.text.toString()
        if (phoneNumber != null) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            
            signInWithPhoneAuthCredential(credential)
        }
        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }




        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            val otpIntent=Intent(this@SignInActivity, OtpActivity::class.java)
            otpIntent.putExtra("otpcr", verificationId)
            startActivity(otpIntent)
            finish()

            // Save verification ID and resending token so we can use them later
            //storedVerificationId = verificationId
            //resendToken = token
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = task.result?.user
                    send_home()
                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

}