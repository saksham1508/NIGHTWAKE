package com.example.nightwake.data

/**
 * Progression meta-data: Narrative Chapters, Outposts, Daily Quests, Login Rewards, Achievements.
 */
data class ChapterStory(
    val chapterNumber: Int,
    val title: String,
    val narrativeIntro: String,
    val targetOutpost: String,
    val rewardScrap: Int,
    val rewardNanites: Int
)

data class DailyQuest(
    val id: String,
    val title: String,
    val requirement: String,
    val targetCount: Int,
    var currentProgress: Int = 0,
    val rewardScrap: Int,
    val rewardMetal: Int,
    val isCompleted: Boolean = false
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val rewardNanites: Int,
    val isClaimed: Boolean = false
)

object ProgressionRegistry {
    val CHAPTERS = listOf(
        ChapterStory(
            chapterNumber = 1,
            title = "Chapter 1: The Ash Awakening",
            narrativeIntro = "Day zero arrived without warning. The regional power grid collapsed, plunging Sector 01 into an unyielding twilight. You and a handful of survivors established Outpost Dawn. To secure power cells, you must push through the swarm and reach the relay tower.",
            targetOutpost = "Radio Tower Alpha",
            rewardScrap = 500,
            rewardNanites = 30
        ),
        ChapterStory(
            chapterNumber = 2,
            title = "Chapter 2: The Toxic Inversion",
            narrativeIntro = "The chemical reservoirs ruptured in the eastern marshlands, breeding mutated leapers and bloated bile spitters. An SOS beacon is transmitting from an underground research vault.",
            targetOutpost = "Bio-Vault Delta",
            rewardScrap = 1000,
            rewardNanites = 50
        ),
        ChapterStory(
            chapterNumber = 3,
            title = "Chapter 3: Apex Nightfall",
            narrativeIntro = "A colossal mechanical horror known as the Corpse Engine is converging toward the sanctuary perimeter. Fortify your turrets, gather every bullet, and withstand the midnight apex.",
            targetOutpost = "The Foundry Bastion",
            rewardScrap = 2500,
            rewardNanites = 100
        )
    )

    fun getDefaultQuests(): List<DailyQuest> = listOf(
        DailyQuest(
            id = "quest_kills_100",
            title = "Pest Control",
            requirement = "Eliminate 150 infected hostiles in any run",
            targetCount = 150,
            currentProgress = 0,
            rewardScrap = 200,
            rewardMetal = 50
        ),
        DailyQuest(
            id = "quest_survive_night",
            title = "Dawn Survivor",
            requirement = "Survive at least 1 full Night cycle (180s)",
            targetCount = 1,
            currentProgress = 0,
            rewardScrap = 300,
            rewardMetal = 75
        ),
        DailyQuest(
            id = "quest_boss_slayer",
            title = "Apex Hunter",
            requirement = "Defeat the Gorgon Behemoth boss",
            targetCount = 1,
            currentProgress = 0,
            rewardScrap = 500,
            rewardMetal = 120
        )
    )

    val ACHIEVEMENTS = listOf(
        AchievementItem(
            id = "ach_first_blood",
            title = "First Midnight",
            description = "Kill your first infected in under 10 seconds of landing.",
            rewardNanites = 10
        ),
        AchievementItem(
            id = "ach_level_10",
            title = "Combat Adaptation",
            description = "Reach Level 10 in a single survival run.",
            rewardNanites = 15
        ),
        AchievementItem(
            id = "ach_evolution",
            title = "Arsenal Transmutation",
            description = "Evolve any weapon into its legendary synergy form.",
            rewardNanites = 25
        ),
        AchievementItem(
            id = "ach_boss_kill",
            title = "Titan Down",
            description = "Slay the Gorgon Behemoth.",
            rewardNanites = 35
        ),
        AchievementItem(
            id = "ach_fortify",
            title = "Bastion Architect",
            description = "Upgrade all Sanctuary structures to Level 2.",
            rewardNanites = 50
        )
    )
}
