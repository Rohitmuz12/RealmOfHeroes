package com.example.realmofheroes.model

/**
 * An enemy template for one stage. Unlike player characters, enemies don't level up
 * via XP — their stats are authored directly per-stage to control difficulty pacing.
 */
data class EnemyTemplate(
    val id: String,
    val name: String,
    val title: String,
    val maxHp: Int,
    val maxMana: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val skills: List<Skill>
) {
    fun toStats(): CharacterStats = CharacterStats(maxHp, maxMana, attack, defense, speed)
}

/**
 * One story stage: a single 1v1 battle against an authored enemy, with rewards
 * for winning. Stages are sequential — `stageNumber` also gates which stages
 * are playable (must clear N-1 before N unlocks), tracked in save data.
 */
data class Stage(
    val stageNumber: Int,
    val name: String,
    val description: String,
    val enemy: EnemyTemplate,
    val currencyReward: Int,
    val xpReward: Int
)
