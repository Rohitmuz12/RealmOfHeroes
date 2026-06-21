package com.example.realmofheroes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.databinding.ItemStageCardBinding
import com.example.realmofheroes.model.Stage

class StageAdapter(
    private val stages: List<Stage>,
    private val saveManager: SaveManager,
    private val onStageSelected: (Stage) -> Unit
) : RecyclerView.Adapter<StageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStageCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemStageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = stages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stage = stages[position]
        val binding = holder.binding
        val unlocked = saveManager.isStageUnlocked(stage.stageNumber)
        val cleared = stage.stageNumber <= saveManager.highestStageCleared

        binding.stageNameText.text = "Stage ${stage.stageNumber}: ${stage.name}"
        binding.stageDescText.text = "${stage.enemy.name} — ${stage.description}"
        binding.stageRewardText.text = "Reward: ${stage.currencyReward} gold, ${stage.xpReward} XP" +
            if (cleared) "  •  Cleared" else ""

        binding.stageActionButton.isEnabled = unlocked
        binding.stageActionButton.text = when {
            !unlocked -> "Locked"
            cleared -> "Replay"
            else -> "Fight"
        }
        binding.stageActionButton.alpha = if (unlocked) 1f else 0.5f

        if (unlocked) {
            binding.stageActionButton.setOnClickListener { onStageSelected(stage) }
        } else {
            binding.stageActionButton.setOnClickListener(null)
        }
    }
}
