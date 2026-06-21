package com.example.realmofheroes.engine

import com.example.realmofheroes.model.Combatant
import com.example.realmofheroes.model.Skill
import kotlin.random.Random

/**
 * Picks a skill for the enemy each round. Kept deliberately simple for Phase 1:
 * prefer a damaging skill it can afford and isn't on cooldown, falling back to
 * the basic attack (which always has 0 cost/cooldown) if nothing else is usable.
 * This lives separate from BattleEngine so smarter AI strategies can be swapped
 * in later without touching battle resolution logic.
 */
object EnemyAi {

    fun chooseSkill(enemy: Combatant): Skill {
        val usableSkills = enemy.skills.filter { enemy.canUseSkill(it) }
        if (usableSkills.isEmpty()) {
            // Guaranteed to exist: every character/enemy template includes a free basic attack.
            return enemy.skills.first { it.isBasicAttack() }
        }

        // Bias toward using a "real" skill over the basic attack when available,
        // so battles don't feel like the enemy is just spamming one move.
        val nonBasicSkills = usableSkills.filter { !it.isBasicAttack() }
        return if (nonBasicSkills.isNotEmpty() && Random.nextFloat() < 0.7f) {
            nonBasicSkills.random()
        } else {
            usableSkills.random()
        }
    }
}
