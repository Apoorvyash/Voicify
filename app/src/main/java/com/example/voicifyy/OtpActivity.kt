package com.example.voicifyy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.voicifyy.databinding.ActivityOtpBinding
import com.example.voicifyy.databinding.ActivitySignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser?=null
    private var phoneNumber:String =""
    private var authId: String=""
    private lateinit var verificationId: String
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()
        val builder=AlertDialog.Builder(this)
        builder.setMessage("Please Wait...")
        builder.setTitle("Loading")
        builder.setCancelable(false)

        dialog=builder.create()
        dialog.show()
        val phoneNumber="+91"+intent.getStringExtra("number")
        val options=PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dialog.dismiss()
                    Toast.makeText(this@OtpActivity, "Please Try again!!${p0}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    dialog.dismiss()
                    verificationId=p0
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.button.setOnClickListener{
            if(binding.number.text!!.isEmpty()){

                Toast.makeText(this, "Please Enter Otp", Toast.LENGTH_SHORT).show()
            }
            else{
                dialog.show()
                val credential=PhoneAuthProvider.getCredential(verificationId, binding.number.text!!.toString())
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            dialog.dismiss()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {

                            Toast.makeText(this, "Please Enter Otp", Toast.LENGTH_SHORT).show()

                            // Update UI
                        }
                    }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

    }
    fun send_home(){
        val loginIntent = Intent(this@OtpActivity, MainActivity::class.java)
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

    fun VerifyOtp(view: View) {
        val otp:String = binding.number.text.toString()
        if(otp.isNotEmpty()){
            val credential=PhoneAuthProvider.getCredential(authId, otp)
            signInWithPhoneAuthCredential(credential)
        }
    }
}