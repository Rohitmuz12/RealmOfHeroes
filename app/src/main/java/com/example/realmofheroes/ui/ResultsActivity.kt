package com.example.realmofheroes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.realmofheroes.databinding.ActivityResultsBinding

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val victory = intent.getBooleanExtra(EXTRA_VICTORY, false)
        val characterId = intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: error("ResultsActivity requires EXTRA_CHARACTER_ID")

        if (victory) {
            val currencyEarned = intent.getIntExtra(EXTRA_CURRENCY_EARNED, 0)
            val xpEarned = intent.getIntExtra(EXTRA_XP_EARNED, 0)
            val levelsGained = intent.getIntExtra(EXTRA_LEVELS_GAINED, 0)

            binding.resultTitleText.text = "VICTORY"
            binding.rewardsPanel.visibility = android.view.View.VISIBLE
            binding.currencyEarnedText.text = "+$currencyEarned Gold"
            binding.xpEarnedText.text = "+$xpEarned XP"

            if (levelsGained > 0) {
                binding.levelUpText.visibility = android.view.View.VISIBLE
                binding.levelUpText.text = if (levelsGained == 1) "Level Up!" else "Leveled up $levelsGained times!"
            } else {
                binding.levelUpText.visibility = android.view.View.GONE
            }

            binding.continueButton.text = "Next Stage"
        } else {
            binding.resultTitleText.text = "DEFEAT"
            binding.rewardsPanel.visibility = android.view.View.GONE
            binding.continueButton.text = "Try Again"
        }

        binding.continueButton.setOnClickListener {
            val intent = Intent(this, StageSelectActivity::class.java)
            intent.putExtra(StageSelectActivity.EXTRA_CHARACTER_ID, characterId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        binding.menuButton.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_VICTORY = "victory"
        const val EXTRA_CHARACTER_ID = "character_id"
        const val EXTRA_CURRENCY_EARNED = "currency_earned"
        const val EXTRA_XP_EARNED = "xp_earned"
        const val EXTRA_LEVELS_GAINED = "levels_gained"
    }
}
