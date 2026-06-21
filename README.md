# Realm of Heroes — Phase 1

A fantasy turn-based RPG for Android. Pick a hero, fight through story stages,
earn gold and XP, level up, and unlock new heroes.

This is **Phase 1**: a complete, working vertical slice — not the full 20+ stage
game yet, but every system (battle engine, skills, status effects, leveling,
currency, unlocks, save persistence) is fully built and playable end to end.

## What's included in Phase 1

- **3 playable heroes**, one per major archetype:
  - **Brodin the Warrior** — aggressive melee, free starter character
  - **Elowen the Mage** — high burst magic damage, fragile (150 gold to unlock)
  - **Garrick the Knight** — tanky defender with sustain (150 gold to unlock)
- **5 story stages** with scaling enemies (goblin → wolf → bandit → ogre → fallen knight)
- **Full battle system**: mana costs, skill cooldowns, speed-based turn order,
  critical hits, status effects (poison, burn, stun, attack/defense buffs & debuffs, regen)
- **Leveling**: XP from battles increases character level, which scales HP/mana/attack/defense
- **Currency & unlocks**: gold earned from battles unlocks new heroes
- **Persistent save**: progress survives app restarts (SharedPreferences + Gson)

## Architecture

```
model/       Pure data: Character, Skill, StatusEffect, Combatant, Stage, Enemy
engine/      BattleEngine (turn resolution) + EnemyAi (skill selection) — no Android dependencies
data/        CharacterRoster, StageRoster (content), SaveManager (persistence)
ui/          5 Activities + 2 RecyclerView adapters
```

The `engine/` package is deliberately UI-agnostic — `BattleEngine` only knows about
`Combatant` and `Skill` objects, not Android views. This means it could be unit
tested directly, and it's also what will make local 2-player mode possible later
without rewriting battle logic.

### Screen flow
```
MainMenuActivity -> CharacterSelectActivity -> StageSelectActivity -> BattleActivity -> ResultsActivity
                                                       ^________________________________|
                                                       (loops back after each battle)
```

## How to build & run

Same process as before:

1. Open this folder in **Android Studio**, let Gradle sync, hit Run — or
2. Build the APK in the cloud via the included **GitHub Actions** workflow
   (`.github/workflows/build.yml`) — push this repo to GitHub and check the
   **Actions** tab for a downloadable `realm-of-heroes-debug-apk` artifact.

## Easy tweaks

- **Add a new skill** → define it as a `Skill(...)` in `CharacterRoster.kt` or
  `StageRoster.kt`, then add it to a character's or enemy's `skills` list
- **Change a character's stats/growth** → edit the relevant `CharacterTemplate(...)`
  in `CharacterRoster.kt`
- **Add a new stage** → add a `Stage(...)` entry to `StageRoster.stages`; it
  automatically becomes playable once the previous stage is cleared
- **Change unlock cost / starting currency** → `unlockCost` on `CharacterTemplate`,
  and `SaveManager.currency` defaults to 0 on a fresh save
- **Tune difficulty** → enemy `maxHp`/`attack`/`defense`/`speed` in `StageRoster.kt`,
  or the XP curve in `CharacterSaveState.xpToNextLevel()`

## What's NOT in Phase 1 (planned for later)

- Stages 6–20+ (currently 5; the system scales, content doesn't exist yet)
- The remaining 3+ characters to reach the "6+" target (rogue, beast, cleric
  classes are defined in the `CharacterClass` enum but have no templates yet)
- Local 2-player mode
- Battle animations/visual polish (currently text log + progress bars, functional but plain)
- Sound

## Known design notes

- Turn order is decided by `speed` stat each round; ties favor the player
- Status effects of the same type refresh duration rather than stacking
  (e.g. reapplying poison resets its 3-turn timer instead of adding a second
  poison instance) — keeps the battle log readable
- If a skill is on cooldown or unaffordable, its button is disabled in the UI,
  but the engine also defends against this in `BattleEngine.performAction`
  in case it's ever called from elsewhere
