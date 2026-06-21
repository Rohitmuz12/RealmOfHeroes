package com.example.realmofheroes.data

import com.example.realmofheroes.model.*

/**
 * Static character roster. Phase 1 ships 3 fully-built characters (one per major
 * archetype: aggressive melee, burst magic, tanky defender) to prove the skill/status
 * system handles meaningfully different kits. More characters slot in here later
 * without touching the battle engine.
 */
object CharacterRoster {

    private val warriorBasicAttack = Skill(
        id = "warrior_basic", name = "Slash",
        description = "A straightforward sword strike.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 8
    )
    private val warriorRage = Skill(
        id = "warrior_rage", name = "Battle Rage",
        description = "Empowers your next attacks, but lowers your guard.",
        manaCost = 15, cooldownTurns = 3,
        targetType = SkillTargetType.SELF, damageType = DamageType.NONE,
        basePower = 0,
        statusEffectToApply = StatusEffect(StatusEffectType.ATTACK_UP, 0.4f, 3, "Battle Rage")
    )
    private val warriorCleave = Skill(
        id = "warrior_cleave", name = "Heavy Cleave",
        description = "A powerful overhead strike.",
        manaCost = 20, cooldownTurns = 2,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 26
    )

    private val mageBasicAttack = Skill(
        id = "mage_basic", name = "Arcane Bolt",
        description = "A small bolt of raw mana.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.MAGICAL,
        basePower = 6
    )
    private val mageFireball = Skill(
        id = "mage_fireball", name = "Fireball",
        description = "Hurls a blazing orb that burns the target over time.",
        manaCost = 25, cooldownTurns = 2,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.MAGICAL,
        basePower = 18,
        statusEffectToApply = StatusEffect(StatusEffectType.BURN, 6f, 3, "Fireball")
    )
    private val mageFrostNova = Skill(
        id = "mage_frostnova", name = "Frost Nova",
        description = "Freezes the target solid, stunning them.",
        manaCost = 30, cooldownTurns = 4,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.MAGICAL,
        basePower = 10,
        statusEffectToApply = StatusEffect(StatusEffectType.STUN, 0f, 1, "Frost Nova")
    )

    private val knightBasicAttack = Skill(
        id = "knight_basic", name = "Shield Bash",
        description = "A solid bash with your shield.",
        manaCost = 0, cooldownTurns = 0,
        targetType = SkillTargetType.ENEMY, damageType = DamageType.PHYSICAL,
        basePower = 7
    )
    private val knightFortify = Skill(
        id = "knight_fortify", name = "Fortify",
        description = "Hunker down, sharply raising your defense.",
        manaCost = 15, cooldownTurns = 3,
        targetType = SkillTargetType.SELF, damageType = DamageType.NONE,
        basePower = 0,
        statusEffectToApply = StatusEffect(StatusEffectType.DEFENSE_UP, 0.5f, 3, "Fortify")
    )
    private val knightRenewal = Skill(
        id = "knight_renewal", name = "Renewal",
        description = "A blessing that slowly mends your wounds.",
        manaCost = 20, cooldownTurns = 4,
        targetType = SkillTargetType.SELF, damageType = DamageType.NONE,
        basePower = 0,
        statusEffectToApply = StatusEffect(StatusEffectType.REGEN, 10f, 3, "Renewal")
    )

    val all: List<CharacterTemplate> = listOf(
        CharacterTemplate(
            id = "warrior",
            name = "Brodin",
            charClass = CharacterClass.WARRIOR,
            title = "Blade of the Frontier",
            lore = "A wandering swordsman who fights with raw aggression, growing stronger as battle rages on.",
            unlockCost = 0, // starter character
            baseMaxHp = 110, baseMaxMana = 50, baseAttack = 16, baseDefense = 8, baseSpeed = 10,
            hpGrowthPerLevel = 12, manaGrowthPerLevel = 4, attackGrowthPerLevel = 3, defenseGrowthPerLevel = 1,
            skills = listOf(warriorBasicAttack, warriorRage, warriorCleave)
        ),
        CharacterTemplate(
            id = "mage",
            name = "Elowen",
            charClass = CharacterClass.MAGE,
            title = "Keeper of the Frostflame",
            lore = "A scholar of both fire and frost, dealing devastating burst damage at the cost of fragility.",
            unlockCost = 150,
            baseMaxHp = 75, baseMaxMana = 90, baseAttack = 20, baseDefense = 4, baseSpeed = 12,
            hpGrowthPerLevel = 7, manaGrowthPerLevel = 8, attackGrowthPerLevel = 4, defenseGrowthPerLevel = 1,
            skills = listOf(mageBasicAttack, mageFireball, mageFrostNova)
        ),
        CharacterTemplate(
            id = "knight",
            name = "Garrick",
            charClass = CharacterClass.KNIGHT,
            title = "Shield of the Old Wall",
            lore = "An unshakeable defender who outlasts foes through sheer endurance.",
            unlockCost = 150,
            baseMaxHp = 140, baseMaxMana = 60, baseAttack = 11, baseDefense = 14, baseSpeed = 7,
            hpGrowthPerLevel = 16, manaGrowthPerLevel = 5, attackGrowthPerLevel = 2, defenseGrowthPerLevel = 3,
            skills = listOf(knightBasicAttack, knightFortify, knightRenewal)
        )
    )

    fun byId(id: String): CharacterTemplate = all.first { it.id == id }
}
