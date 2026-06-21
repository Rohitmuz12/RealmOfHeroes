package com.example.realmofheroes.model

/**
 * Runtime battle state for one participant (player character OR enemy — both become
 * this same shape once a battle starts, which keeps the battle engine's logic
 * symmetric instead of having separate player/enemy code paths).
 */
class Combatant(
    val name: String,
    val baseStats: CharacterStats,
    val skills: List<Skill>,
    val isPlayer: Boolean
) {
    var currentHp: Int = baseStats.maxHp
    var currentMana: Int = baseStats.maxMana
    val statusEffects: MutableList<StatusEffect> = mutableListOf()
    val skillCooldowns: MutableMap<String, Int> = mutableMapOf() // skillId -> turns remaining

    val isAlive: Boolean get() = currentHp > 0

    /** Effective attack after applying any ATTACK_UP/ATTACK_DOWN status effects. */
    fun effectiveAttack(): Int {
        var multiplier = 1f
        for (effect in statusEffects) {
            if (effect.type == StatusEffectType.ATTACK_UP) multiplier += effect.magnitude
            if (effect.type == StatusEffectType.ATTACK_DOWN) multiplier -= effect.magnitude
        }
        return (baseStats.attack * multiplier.coerceAtLeast(0.1f)).toInt()
    }

    /** Effective defense after applying any DEFENSE_UP/DEFENSE_DOWN status effects. */
    fun effectiveDefense(): Int {
        var multiplier = 1f
        for (effect in statusEffects) {
            if (effect.type == StatusEffectType.DEFENSE_UP) multiplier += effect.magnitude
            if (effect.type == StatusEffectType.DEFENSE_DOWN) multiplier -= effect.magnitude
        }
        return (baseStats.defense * multiplier.coerceAtLeast(0f)).toInt()
    }

    fun isStunned(): Boolean = statusEffects.any { it.type == StatusEffectType.STUN }

    fun canAffordSkill(skill: Skill): Boolean = currentMana >= skill.manaCost

    fun isSkillOnCooldown(skill: Skill): Boolean = (skillCooldowns[skill.id] ?: 0) > 0

    fun canUseSkill(skill: Skill): Boolean = canAffordSkill(skill) && !isSkillOnCooldown(skill)

    fun applyDamage(amount: Int) {
        currentHp = (currentHp - amount).coerceAtLeast(0)
    }

    fun applyHeal(amount: Int) {
        currentHp = (currentHp + amount).coerceAtMost(baseStats.maxHp)
    }

    fun spendMana(amount: Int) {
        currentMana = (currentMana - amount).coerceAtLeast(0)
    }

    fun startCooldown(skill: Skill) {
        if (skill.cooldownTurns > 0) {
            skillCooldowns[skill.id] = skill.cooldownTurns
        }
    }

    fun addStatusEffect(effect: StatusEffect) {
        // Stacking rule: DoTs and stat modifiers of the same type refresh duration and
        // take the new magnitude rather than stacking infinitely, keeping battles readable.
        val existing = statusEffects.find { it.type == effect.type }
        if (existing != null) {
            statusEffects.remove(existing)
        }
        statusEffects.add(effect)
    }

    /**
     * Called at the end of this combatant's turn: ticks down DoT/HoT effects, stat
     * modifiers, and cooldowns, removing expired ones. Returns log messages describing
     * what happened, for the battle UI to display.
     *
     * STUN is deliberately excluded here — see tickStun().
     */
    fun tickEndOfTurn(): List<String> {
        val messages = mutableListOf<String>()

        // STUN is excluded from this loop entirely — it's ticked separately by
        // tickStun(), only when it actually blocks an action. See tickStun() for why.
        val effectIterator = statusEffects.iterator()
        while (effectIterator.hasNext()) {
            val effect = effectIterator.next()
            if (effect.type == StatusEffectType.STUN) continue

            when (effect.type) {
                StatusEffectType.POISON, StatusEffectType.BURN -> {
                    val dmg = effect.magnitude.toInt()
                    applyDamage(dmg)
                    messages.add("$name takes $dmg damage from ${effect.type.name.lowercase()}")
                }
                StatusEffectType.REGEN -> {
                    val heal = effect.magnitude.toInt()
                    applyHeal(heal)
                    messages.add("$name regenerates $heal HP")
                }
                else -> { /* stat modifiers don't tick damage, just expire below */ }
            }
            effect.remainingTurns -= 1
            if (effect.remainingTurns <= 0) {
                effectIterator.remove()
            }
        }

        val cooldownKeys = skillCooldowns.keys.toList()
        for (key in cooldownKeys) {
            val remaining = (skillCooldowns[key] ?: 0) - 1
            if (remaining <= 0) skillCooldowns.remove(key) else skillCooldowns[key] = remaining
        }

        return messages
    }

    /**
     * Decrements STUN duration. Called only when a stun actually blocks this
     * combatant's action (from BattleEngine.performAction), NOT at end-of-round.
     * This ensures a 1-turn stun blocks exactly one action before expiring —
     * if it ticked at end-of-round instead, it would expire before the victim
     * ever got a turn, making stun a no-op.
     */
    fun tickStun() {
        val stunEffects = statusEffects.filter { it.type == StatusEffectType.STUN }
        for (effect in stunEffects) {
            effect.remainingTurns -= 1
            if (effect.remainingTurns <= 0) {
                statusEffects.remove(effect)
            }
        }
    }
}
