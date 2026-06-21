package com.example.realmofheroes.model

enum class CharacterClass {
    WARRIOR, MAGE, KNIGHT, ROGUE, BEAST, CLERIC
}

/**
 * The unchanging "design" of a character: base stats at level 1, growth per level,
 * skill list, unlock cost, lore. This is template data shared by every player who
 * has (or hasn't) unlocked this character — think of it as the character's class
 * definition, not any one player's save data.
 */
data class CharacterTemplate(
    val id: String,
    val name: String,
    val charClass: CharacterClass,
    val title: String,              // flavor subtitle, e.g. "Blade of the Frontier"
    val lore: String,
    val unlockCost: Int,            // currency cost to unlock; 0 = unlocked from the start
    val baseMaxHp: Int,
    val baseMaxMana: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val baseSpeed: Int,             // determines turn order
    val hpGrowthPerLevel: Int,
    val manaGrowthPerLevel: Int,
    val attackGrowthPerLevel: Int,
    val defenseGrowthPerLevel: Int,
    val skills: List<Skill>
) {
    fun statsAtLevel(level: Int): CharacterStats {
        val growthSteps = (level - 1).coerceAtLeast(0)
        return CharacterStats(
            maxHp = baseMaxHp + hpGrowthPerLevel * growthSteps,
            maxMana = baseMaxMana + manaGrowthPerLevel * growthSteps,
            attack = baseAttack + attackGrowthPerLevel * growthSteps,
            defense = baseDefense + defenseGrowthPerLevel * growthSteps,
            speed = baseSpeed
        )
    }
}

/** Computed stats for a character at a specific level — used by both player characters and enemies. */
data class CharacterStats(
    val maxHp: Int,
    val maxMana: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int
)

/**
 * A player's save-data record for one character: level, XP, and unlock status.
 * This is what actually gets persisted — combine with the matching CharacterTemplate
 * (looked up by id) to get full stats.
 */
data class CharacterSaveState(
    val templateId: String,
    var level: Int = 1,
    var xp: Int = 0,
    var unlocked: Boolean = false
) {
    companion object {
        /** XP required to advance from `level` to `level + 1`. Simple increasing curve. */
        fun xpToNextLevel(level: Int): Int = 50 + (level - 1) * 30
    }

    fun addXp(amount: Int): Int {
        var levelsGained = 0
        xp += amount
        while (xp >= xpToNextLevel(level)) {
            xp -= xpToNextLevel(level)
            level += 1
            levelsGained += 1
        }
        return levelsGained
    }
}
