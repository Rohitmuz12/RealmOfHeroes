package com.example.realmofheroes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realmofheroes.data.CharacterRoster
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.databinding.ActivityCharacterSelectBinding

class CharacterSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterSelectBinding
    private lateinit var saveManager: SaveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)
        val browseOnly = intent.getBooleanExtra(EXTRA_BROWSE_ONLY, false)

        binding.characterRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.characterRecyclerView.adapter = CharacterAdapter(
            CharacterRoster.all, saveManager, browseOnly
        ) { selectedTemplate ->
            if (browseOnly) return@CharacterAdapter
            val intent = Intent(this, StageSelectActivity::class.java)
            intent.putExtra(StageSelectActivity.EXTRA_CHARACTER_ID, selectedTemplate.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.currencyText.text = "Gold: ${saveManager.currency}"
        binding.characterRecyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val EXTRA_BROWSE_ONLY = "browse_only"
    }
}
