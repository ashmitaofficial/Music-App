package com.example.musicapp

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//
        setContentView(R.layout.activity_home)
//        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
//        binding.root.addDrawerListener(toggle)
//        toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        supportFragmentManager.beginTransaction()
            .add(R.id.container, HomeFragment::class.java, null)
            .commit()


    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }

}