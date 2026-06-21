package com.example.realmofheroes.engine

import com.example.realmofheroes.model.Combatant
import com.example.realmofheroes.model.DamageType
import com.example.realmofheroes.model.Skill
import com.example.realmofheroes.model.SkillTargetType
import kotlin.random.Random

enum class BattleOutcome { ONGOING, PLAYER_WON, ENEMY_WON }

/**
 * Result of resolving one skill use, bundled with log messages so the UI can
 * narrate what happened without needing to inspect combatant internals directly.
 */
data class ActionResult(
    val messages: List<String>,
    val outcome: BattleOutcome
)

/**
 * Drives a single 1v1 battle: turn order by speed, mana/cooldown gating, damage
 * calculation, status effect application, and end-of-turn ticking. The engine is
 * UI-agnostic — it only knows about Combatants and Skills, so it can be unit-tested
 * or reused by a future multiplayer mode without touching any Android view code.
 */
class BattleEngine(
    val player: Combatant,
    val enemy: Combatant
) {
    private val log = mutableListOf<String>()

    /** Whoever has higher speed acts first each round; ties favor the player for predictability. */
    fun playerActsFirst(): Boolean = player.baseStats.speed >= enemy.baseStats.speed

    fun checkOutcome(): BattleOutcome {
        return when {
            !player.isAlive -> BattleOutcome.ENEMY_WON
            !enemy.isAlive -> BattleOutcome.PLAYER_WON
            else -> BattleOutcome.ONGOING
        }
    }

    /**
     * Resolves one full round: both combatants act once (in speed order), then both
     * tick end-of-turn effects. If a combatant is stunned, their action is skipped
     * but they still tick down their stun duration.
     *
     * @param playerSkill the skill the player chose this round.
     * @param enemySkill the skill the enemy AI chose this round (selected by EnemyAi).
     */
    fun resolveRound(playerSkill: Skill, enemySkill: Skill): ActionResult {
        log.clear()
        val firstActor = if (playerActsFirst()) player else enemy
        val secondActor = if (playerActsFirst()) enemy else player
        val firstSkill = if (playerActsFirst()) playerSkill else enemySkill
        val secondSkill = if (playerActsFirst()) enemySkill else playerSkill
        val firstTarget = if (firstActor === player) enemy else player
        val secondTarget = if (secondActor === player) enemy else player

        performAction(firstActor, firstTarget, firstSkill)
        if (checkOutcome() != BattleOutcome.ONGOING) {
            return ActionResult(log.toList(), checkOutcome())
        }

        performAction(secondActor, secondTarget, secondSkill)
        if (checkOutcome() != BattleOutcome.ONGOING) {
            return ActionResult(log.toList(), checkOutcome())
        }

        // End-of-round ticking for both sides
        log.addAll(player.tickEndOfTurn())
        log.addAll(enemy.tickEndOfTurn())

        return ActionResult(log.toList(), checkOutcome())
    }

    private fun performAction(actor: Combatant, target: Combatant, skill: Skill) {
        if (!actor.isAlive) return

        if (actor.isStunned()) {
            log.add("${actor.name} is stunned and cannot act!")
            actor.tickStun()
            return
        }

        if (!actor.canUseSkill(skill)) {
            // Should not normally happen (UI/AI should filter unusable skills), but
            // fall back to a free "struggle" action so a round never silently does nothing.
            log.add("${actor.name} fumbles and loses their turn!")
            return
        }

        actor.spendMana(skill.manaCost)
        actor.startCooldown(skill)

        when (skill.damageType) {
            DamageType.PHYSICAL, DamageType.MAGICAL -> {
                val rawDamage = (actor.effectiveAttack() + skill.basePower - target.effectiveDefense())
                    .coerceAtLeast(1)
                val isCrit = Random.nextFloat() < 0.12f
                val finalDamage = if (isCrit) (rawDamage * 1.5f).toInt() else rawDamage
                target.applyDamage(finalDamage)
                log.add(
                    if (isCrit) "${actor.name} uses ${skill.name} — CRITICAL HIT for $finalDamage damage!"
                    else "${actor.name} uses ${skill.name} for $finalDamage damage."
                )
            }
            DamageType.NONE -> {
                if (skill.healAmount > 0) {
                    actor.applyHeal(skill.healAmount)
                    log.add("${actor.name} uses ${skill.name} and heals for ${skill.healAmount} HP.")
                } else {
                    log.add("${actor.name} uses ${skill.name}.")
                }
            }
        }

        skill.statusEffectToApply?.let { effectTemplate ->
            val effectTarget = if (skill.targetType == SkillTargetType.SELF) actor else target
            // Skills are shared singleton objects (defined once in CharacterRoster/StageRoster),
            // so effectTemplate is the SAME StatusEffect instance every time this skill is used,
            // anywhere. We must copy() it before adding to a combatant's effect list — otherwise
            // every combatant who's ever hit by this skill would share one mutable StatusEffect,
            // and ticking it down for one combatant would corrupt it for all the others.
            effectTarget.addStatusEffect(effectTemplate.copy())
            log.add("${effectTarget.name} is afflicted with ${effectTemplate.type.name.lowercase()}!")
        }
    }
}
