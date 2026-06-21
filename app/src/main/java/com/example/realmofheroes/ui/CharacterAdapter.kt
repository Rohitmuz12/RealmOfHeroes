package com.example.realmofheroes.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.databinding.ItemCharacterCardBinding
import com.example.realmofheroes.model.CharacterClass
import com.example.realmofheroes.model.CharacterTemplate

class CharacterAdapter(
    private val templates: List<CharacterTemplate>,
    private val saveManager: SaveManager,
    private val browseOnly: Boolean,
    private val onSelected: (CharacterTemplate) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCharacterCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemCharacterCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = templates.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templates[position]
        val saveState = saveManager.getCharacterState(template.id)
        val binding = holder.binding

        binding.nameText.text = template.name
        binding.titleText.text = template.title
        binding.loreText.text = template.lore
        binding.classAccent.setBackgroundColor(accentColorFor(template.charClass))

        if (saveState.unlocked) {
            binding.levelText.text = "Level ${saveState.level}"
            binding.actionButton.text = if (browseOnly) "View" else "Select"
            binding.actionButton.isEnabled = true
            binding.actionButton.setOnClickListener { onSelected(template) }
        } else {
            binding.levelText.text = "Locked — costs ${template.unlockCost} gold"
            binding.actionButton.text = "Unlock"
            binding.actionButton.isEnabled = true
            binding.actionButton.setOnClickListener {
                val success = saveManager.unlockCharacter(template.id)
                if (success) {
                    notifyItemChanged(position)
                } else {
                    binding.levelText.text = "Need ${template.unlockCost - saveManager.currency} more gold"
                }
            }
        }
    }

    private fun accentColorFor(charClass: CharacterClass): Int {
        return when (charClass) {
            CharacterClass.WARRIOR -> Color.parseColor("#E86B4B")
            CharacterClass.MAGE -> Color.parseColor("#8C6BE8")
            CharacterClass.KNIGHT -> Color.parseColor("#6B9CE8")
            CharacterClass.ROGUE -> Color.parseColor("#6BE8A0")
            CharacterClass.BEAST -> Color.parseColor("#E8D06B")
            CharacterClass.CLERIC -> Color.parseColor("#E8E8E8")
        }
    }
}
