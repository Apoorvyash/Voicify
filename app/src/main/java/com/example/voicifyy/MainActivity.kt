package com.example.voicifyy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.voicifyy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private var currentUser: FirebaseUser?= null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
        currentUser=auth.currentUser
        if(currentUser==null){
            startActivity(Intent(this, SignInActivity::class.java))
        }
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottombar)
        bottomBar.setOnItemSelectedListener() {
            if(it.itemId==R.id.connect){
                inflateFragment(ConnectFragment.newInstance())
            }
            else if (it.itemId==R.id.profile) {
                inflateFragment(ProfileFragment.newInstance())
            }

            true
        }



    }
    fun login() {
        val loginIntent = Intent(this@MainActivity, SignInActivity:: class.java)
        startActivity(loginIntent)
        finish()
    }

    fun logout(view: View){
        auth.signOut()
        login()
    }

    override fun onStart() {
        super.onStart()
        if(currentUser==null){
            login()
        }
        else{
            var userInfo=currentUser!!.phoneNumber
            binding.txtnum.text="Welcome $userInfo"
        }
    }
    private fun inflateFragment(newInstance: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance)
        transaction.commit()

    }
}