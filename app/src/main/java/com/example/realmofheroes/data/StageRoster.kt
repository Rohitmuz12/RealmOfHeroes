package com.example.realmofheroes.data

import com.example.realmofheroes.model.*

/**
 * Story stage roster. Phase 1 ships 5 stages as a vertical slice; the remaining
 * 15+ for the full "20+ stages" target get authored the same way in Phase 2 —
 * this function is the only place that needs extending, nothing else references
 * stage count directly.
 */
object StageRoster {

    private val goblinBite = Skill(
        id = "goblin_bite", name = "Bite",
        description = "A vicious bite.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 6
    )
    private val wolfHowl = Skill(
        id = "wolf_howl", name = "Howl",
        description = "A bone-chilling howl that weakens resolve.",
        manaCost = 10, cooldownTurns = 3,
        targetType = SkillTargetType.SELF, damageType = DamageType.NONE,
        basePower = 0,
        statusEffectToApply = StatusEffect(StatusEffectType.ATTACK_UP, 0.3f, 3, "Howl")
    )
    private val wolfClaw = Skill(
        id = "wolf_claw", name = "Rending Claw",
        description = "Tears at the target, causing bleeding.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 9
    )
    private val banditStab = Skill(
        id = "bandit_stab", name = "Dagger Stab",
        description = "A quick, dirty strike.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 10
    )
    private val banditPoison = Skill(
        id = "bandit_poison", name = "Poisoned Blade",
        description = "A coated blade that poisons on contact.",
        manaCost = 15, cooldownTurns = 3,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 8,
        statusEffectToApply = StatusEffect(StatusEffectType.POISON, 5f, 3, "Poisoned Blade")
    )
    private val ogreSlam = Skill(
        id = "ogre_slam", name = "Ground Slam",
        description = "A devastating slam that can stun.",
        manaCost = 20, cooldownTurns = 4,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 14,
        statusEffectToApply = StatusEffect(StatusEffectType.STUN, 0f, 1, "Ground Slam")
    )
    private val ogreCrush = Skill(
        id = "ogre_crush", name = "Crush",
        description = "Brute force, nothing more.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 12
    )
    private val darkKnightSlash = Skill(
        id = "darkknight_slash", name = "Cursed Slash",
        description = "A corrupted blade strike.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 13
    )
    private val darkKnightCurse = Skill(
        id = "darkknight_curse", name = "Weakening Curse",
        description = "Saps the target's strength.",
        manaCost = 18, cooldownTurns = 3,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.NONE,
        basePower = 0,
        statusEffectToApply = StatusEffect(StatusEffectType.ATTACK_DOWN, 0.3f, 3, "Weakening Curse")
    )

    val stages: List<Stage> = listOf(
        Stage(
            stageNumber = 1,
            name = "Forest Path",
            description = "A young goblin blocks the trail, more startled than dangerous.",
            enemy = EnemyTemplate(
                id = "goblin_1", name = "Goblin Scout", title = "",
                maxHp = 60, maxMana = 0, attack = 8, defense = 3, speed = 8,
                skills = listOf(goblinBite)
            ),
            currencyReward = 30, xpReward = 25
        ),
        Stage(
            stageNumber = 2,
            name = "Wolf Den",
            description = "A lone wolf, hungry and aggressive.",
            enemy = EnemyTemplate(
                id = "wolf_1", name = "Grey Wolf", title = "",
                maxHp = 80, maxMana = 30, attack = 11, defense = 4, speed = 13,
                skills = listOf(wolfClaw, wolfHowl)
            ),
            currencyReward = 40, xpReward = 35
        ),
        Stage(
            stageNumber = 3,
            name = "Bandit Camp",
            description = "A bandit thug, quick with a poisoned blade.",
            enemy = EnemyTemplate(
                id = "bandit_1", name = "Bandit Thug", title = "",
                maxHp = 95, maxMana = 40, attack = 13, defense = 6, speed = 11,
                skills = listOf(banditStab, banditPoison)
            ),
            currencyReward = 55, xpReward = 45
        ),
        Stage(
            stageNumber = 4,
            name = "Old Quarry",
            description = "An ogre guards the quarry, slow but devastating.",
            enemy = EnemyTemplate(
                id = "ogre_1", name = "Quarry Ogre", title = "",
                maxHp = 160, maxMana = 30, attack = 15, defense = 8, speed = 5,
                skills = listOf(ogreCrush, ogreSlam)
            ),
            currencyReward = 70, xpReward = 60
        ),
        Stage(
            stageNumber = 5,
            name = "The Fallen Outpost",
            description = "A corrupted knight, the first real test of skill.",
            enemy = EnemyTemplate(
                id = "darkknight_1", name = "Fallen Knight", title = "",
                maxHp = 150, maxMana = 50, attack = 16, defense = 10, speed = 9,
                skills = listOf(darkKnightSlash, darkKnightCurse)
            ),
            currencyReward = 90, xpReward = 80
        )
    )

    fun byStageNumber(number: Int): Stage = stages.first { it.stageNumber == number }
}
