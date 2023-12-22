package com.example.mobileuas

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobileuas.databinding.ActivityMainPublicBinding


class MainPublic : AppCompatActivity() {

    private lateinit var binding: ActivityMainPublicBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPublicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(UserHomeFragment())

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.bottomNavView.setItemSelected(R.id.nav_home)

        binding.bottomNavView.setOnItemSelectedListener {
            when(it){
                R.id.nav_home -> replaceFragment(UserHomeFragment())
                R.id.nav_settings -> replaceFragment(UserSettingsFragment())
            }
            true
        }

        binding.bottomNavView.setItemSelected(R.id.nav_home)

    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_frag, fragment)
        transaction.commit()
    }
}