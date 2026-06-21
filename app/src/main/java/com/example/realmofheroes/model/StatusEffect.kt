package com.example.realmofheroes.model

/**
 * The category of status effect, used to decide how it's applied each turn
 * and how it interacts with stacking rules (e.g. multiple poisons stack damage,
 * but a stun does not stack duration with another stun).
 */
enum class StatusEffectType {
    POISON,       // damage over time, ticks at end of turn
    BURN,         // damage over time, slightly higher than poison but shorter
    STUN,         // skips the afflicted unit's next turn entirely
    ATTACK_UP,    // increases outgoing damage
    ATTACK_DOWN,  // decreases outgoing damage
    DEFENSE_UP,   // reduces incoming damage
    DEFENSE_DOWN, // increases incoming damage
    REGEN         // heals over time, ticks at end of turn
}

/**
 * A single active status effect instance on a combatant.
 *
 * @param magnitude meaning depends on type: damage-per-tick for POISON/BURN/REGEN,
 *                  or a percentage multiplier delta for the *_UP/*_DOWN types
 *                  (e.g. 0.3 = +30% for ATTACK_UP, -30% for ATTACK_DOWN if negative).
 * @param remainingTurns how many more end-of-turn ticks this effect will apply before expiring.
 * @param sourceName who/what applied this, shown in battle log messages.
 */
data class StatusEffect(
    val type: StatusEffectType,
    val magnitude: Float,
    var remainingTurns: Int,
    val sourceName: String
) {
    fun isDamageOverTime(): Boolean = type == StatusEffectType.POISON || type == StatusEffectType.BURN
    fun isStatModifier(): Boolean = type == StatusEffectType.ATTACK_UP || type == StatusEffectType.ATTACK_DOWN ||
            type == StatusEffectType.DEFENSE_UP || type == StatusEffectType.DEFENSE_DOWN
}
