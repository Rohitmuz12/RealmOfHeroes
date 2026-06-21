package com.example.realmofheroes.model

enum class SkillTargetType {
    ENEMY,   // single enemy (the only enemy, in 1v1, but kept explicit for clarity/future team battles)
    SELF,    // the caster
    ALLY     // reserved for future team-battle support
}

enum class DamageType {
    PHYSICAL,
    MAGICAL,
    NONE // pure utility skills (heal/buff) deal no damage
}

/**
 * A single combat ability. Characters carry a fixed list of these; the battle engine
 * reads mana cost / cooldown / effects directly off this model rather than hardcoding
 * per-character logic, so new skills are pure data.
 */
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val manaCost: Int,
    val cooldownTurns: Int,         // turns before this skill can be used again after casting
    val targetType: SkillTargetType,
    val damageType: DamageType,
    val basePower: Int,             // base damage or heal amount before stat scaling
    val statusEffectToApply: StatusEffect? = null,
    val healAmount: Int = 0         // for heal/regen-style utility skills
) {
    /** Skills with 0 cooldown can be used every turn once mana allows it. */
    fun isBasicAttack(): Boolean = cooldownTurns == 0 && manaCost == 0
}
