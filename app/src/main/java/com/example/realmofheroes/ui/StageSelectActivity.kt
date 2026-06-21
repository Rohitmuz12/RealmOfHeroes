package com.example.realmofheroes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.data.StageRoster
import com.example.realmofheroes.databinding.ActivityStageSelectBinding

class StageSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStageSelectBinding
    private lateinit var saveManager: SaveManager
    private lateinit var characterId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStageSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)
        characterId = intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: error("StageSelectActivity requires EXTRA_CHARACTER_ID")

        binding.stageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stageRecyclerView.adapter = StageAdapter(StageRoster.stages, saveManager) { stage ->
            val intent = Intent(this, BattleActivity::class.java)
            intent.putExtra(BattleActivity.EXTRA_CHARACTER_ID, characterId)
            intent.putExtra(BattleActivity.EXTRA_STAGE_NUMBER, stage.stageNumber)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.stageRecyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val EXTRA_CHARACTER_ID = "character_id"
    }
}
