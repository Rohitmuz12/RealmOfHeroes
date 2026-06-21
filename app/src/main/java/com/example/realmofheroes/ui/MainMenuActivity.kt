package com.example.realmofheroes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var saveManager: SaveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)

        binding.playButton.setOnClickListener {
            startActivity(Intent(this, CharacterSelectActivity::class.java))
        }
        binding.charactersButton.setOnClickListener {
            val intent = Intent(this, CharacterSelectActivity::class.java)
            intent.putExtra(CharacterSelectActivity.EXTRA_BROWSE_ONLY, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.currencyText.text = "Gold: ${saveManager.currency}"
    }
}
