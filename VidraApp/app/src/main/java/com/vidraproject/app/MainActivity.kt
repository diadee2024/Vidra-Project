package com.vidraproject.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vidraproject.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTelegram.setOnClickListener {
            openUrl("https://t.me/vidraproject")
        }
        binding.btnYoutube.setOnClickListener {
            openUrl("https://www.youtube.com/@vidraproject")
        }
        binding.btnWebsite.setOnClickListener {
            openUrl("https://github.com/VidraProject/Vidra-Project")
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
