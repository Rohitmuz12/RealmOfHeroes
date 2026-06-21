package com.example.realmofheroes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.realmofheroes.data.CharacterRoster
import com.example.realmofheroes.data.SaveManager
import com.example.realmofheroes.data.StageRoster
import com.example.realmofheroes.databinding.ActivityBattleBinding
import com.example.realmofheroes.engine.BattleEngine
import com.example.realmofheroes.engine.BattleOutcome
import com.example.realmofheroes.engine.EnemyAi
import com.example.realmofheroes.model.Combatant
import com.example.realmofheroes.model.Skill

class BattleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBattleBinding
    private lateinit var saveManager: SaveManager
    private lateinit var engine: BattleEngine
    private lateinit var characterId: String
    private var stageNumber: Int = 1

    private var battleEnded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)
        characterId = intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: error("BattleActivity requires EXTRA_CHARACTER_ID")
        stageNumber = intent.getIntExtra(EXTRA_STAGE_NUMBER, 1)

        val template = CharacterRoster.byId(characterId)
        val saveState = saveManager.getCharacterState(characterId)
        val stage = StageRoster.byStageNumber(stageNumber)

        val playerCombatant = Combatant(
            name = template.name,
            baseStats = template.statsAtLevel(saveState.level),
            skills = template.skills,
            isPlayer = true
        )
        val enemyCombatant = Combatant(
            name = stage.enemy.name,
            baseStats = stage.enemy.toStats(),
            skills = stage.enemy.skills,
            isPlayer = false
        )

        engine = BattleEngine(playerCombatant, enemyCombatant)

        binding.playerNameText.text = template.name
        binding.enemyNameText.text = stage.enemy.name

        setupSkillButtons(playerCombatant)
        refreshUi("${stage.enemy.name} appears! ${stage.description}")
    }

    private fun setupSkillButtons(player: Combatant) {
        val buttons = listOf(binding.skillButton0, binding.skillButton1, binding.skillButton2)
        for ((index, button) in buttons.withIndex()) {
            if (index < player.skills.size) {
                val skill = player.skills[index]
                button.visibility = android.view.View.VISIBLE
                button.setOnClickListener { onPlayerChooseSkill(skill) }
            } else {
                button.visibility = android.view.View.GONE
            }
        }
    }

    private fun onPlayerChooseSkill(skill: Skill) {
        if (battleEnded) return

        val player = engine.player
        if (!player.canUseSkill(skill)) {
            appendLog("You can't use ${skill.name} right now (not enough mana or still on cooldown).")
            return
        }

        val enemySkill = EnemyAi.chooseSkill(engine.enemy)
        val result = engine.resolveRound(skill, enemySkill)

        for (message in result.messages) {
            appendLog(message)
        }

        refreshUi(null)
        updateSkillButtonStates()

        when (result.outcome) {
            BattleOutcome.PLAYER_WON -> onBattleWon()
            BattleOutcome.ENEMY_WON -> onBattleLost()
            BattleOutcome.ONGOING -> { }
        }
    }

    private fun onBattleWon() {
        battleEnded = true
        val stage = StageRoster.byStageNumber(stageNumber)
        saveManager.markStageCleared(stageNumber)
        val levelsGained = saveManager.applyBattleRewards(characterId, stage.currencyReward, stage.xpReward)

        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra(ResultsActivity.EXTRA_VICTORY, true)
        intent.putExtra(ResultsActivity.EXTRA_CURRENCY_EARNED, stage.currencyReward)
        intent.putExtra(ResultsActivity.EXTRA_XP_EARNED, stage.xpReward)
        intent.putExtra(ResultsActivity.EXTRA_LEVELS_GAINED, levelsGained)
        intent.putExtra(ResultsActivity.EXTRA_CHARACTER_ID, characterId)
        startActivity(intent)
        finish()
    }

    private fun onBattleLost() {
        battleEnded = true
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra(ResultsActivity.EXTRA_VICTORY, false)
        intent.putExtra(ResultsActivity.EXTRA_CHARACTER_ID, characterId)
        startActivity(intent)
        finish()
    }

    private fun appendLog(message: String) {
        binding.battleLogText.append("\n$message")
        binding.logScrollView.post {
            binding.logScrollView.fullScroll(android.view.View.FOCUS_DOWN)
        }
    }

    private fun refreshUi(initialMessage: String?) {
        val player = engine.player
        val enemy = engine.enemy

        binding.playerHpBar.max = player.baseStats.maxHp
        binding.playerHpBar.progress = player.currentHp
        binding.playerHpText.text = "HP: ${player.currentHp}/${player.baseStats.maxHp}"

        binding.playerManaBar.max = player.baseStats.maxMana.coerceAtLeast(1)
        binding.playerManaBar.progress = player.currentMana
        binding.playerManaText.text = "MP: ${player.currentMana}/${player.baseStats.maxMana}"

        binding.enemyHpBar.max = enemy.baseStats.maxHp
        binding.enemyHpBar.progress = enemy.currentHp
        binding.enemyHpText.text = "HP: ${enemy.currentHp}/${enemy.baseStats.maxHp}"

        binding.playerStatusText.text = statusSummary(player)
        binding.enemyStatusText.text = statusSummary(enemy)

        if (initialMessage != null) {
            binding.battleLogText.text = initialMessage
        }
    }

    private fun statusSummary(combatant: Combatant): String {
        if (combatant.statusEffects.isEmpty()) return ""
        return combatant.statusEffects.joinToString(", ") {
            "${it.type.name.lowercase()} (${it.remainingTurns})"
        }
    }

    private fun updateSkillButtonStates() {
        val player = engine.player
        val buttons = listOf(binding.skillButton0, binding.skillButton1, binding.skillButton2)
        for ((index, button) in buttons.withIndex()) {
            if (index >= player.skills.size) continue
            val skill = player.skills[index]
            applySkillButtonLabel(button, skill, player)
        }
    }

    private fun applySkillButtonLabel(button: Button, skill: Skill, player: Combatant) {
        val onCooldown = player.isSkillOnCooldown(skill)
        val canAfford = player.canAffordSkill(skill)
        button.isEnabled = onCooldown.not() && canAfford
        button.alpha = if (button.isEnabled) 1f else 0.5f

        val cooldownSuffix = if (onCooldown) " (CD ${player.skillCooldowns[skill.id]})" else ""
        val costSuffix = if (skill.manaCost > 0) " [${skill.manaCost} MP]" else ""
        button.text = "${skill.name}$costSuffix$cooldownSuffix"
    }

    companion object {
        const val EXTRA_CHARACTER_ID = "character_id"
        const val EXTRA_STAGE_NUMBER = "stage_number"
    }
}
