# NIGHTWAKE
> *"Survive the night, fortify at dawn."*

**NIGHTWAKE** is a soft-launch-ready mobile hybrid combining top-down roguelike horde-survival combat with a persistent sanctuary base-building meta-layer for Android.

---

## 🏗️ Architecture Overview

- **Core Engine**: High-performance 60 FPS fixed/variable timestep game loop operating on custom Jetpack Compose `Canvas` with hardware acceleration.
- **Zero-GC Hot Loop**: All combat entities (Enemies, Projectiles, XP Gems, Damage Numbers) utilize pre-allocated object pools and flat primitive arrays.
- **Broadphase Spatial Partitioning**: `SpatialHashGrid` with integer cell keys partitions the 2D arena into buckets, enabling rapid auto-aim queries and multi-unit collision resolution with zero per-frame garbage collection allocations.
- **Decoupled Platform Services**:
  - `ISaveService`: Offline-first versioned JSON save with automatic schema migration and SHA-256 HMAC anti-tamper signature validation.
  - `IAdService`: Frequency-capped rewarded ads (Revive on death, energy refill, daily rewards).
  - `IPurchaseService`: Anti-P2W monetization with Battle Pass and convenience bundles.
  - `IAnalyticsService`: Funnel, retention, run duration, and economy sink/source event tracking.
  - `IRemoteConfigService`: Live tuning and feature flags.
  - `INotificationService`: Base building completions and fuel refills.

---

## 🎮 Game Systems

### A. Combat (Minute-to-Minute)
- **Controls**: Virtual on-screen joystick, auto-aim target acquisition, auto-fire.
- **Weapons**: 6 base weapons (AR-44, Sawed-off Shotgun, Arc Projector, Corrosive Aerosol, Orbiting Saws, Thumper Mortar) + 6 evolutions (Vulcan Overdrive, Apex Flak, Cataclysm Coil, etc.).
- **Upgrades**: 25+ upgrades including weapon passives, area enhancers, cooldown triggers, and survivability perks.
- **Enemies**: 8 horde types (Crawler, Shambler, Spitter, Brute, Leaper, Screamer, Riot Enforcer, Toxic Bloat) + 3 Bosses (Gorgon Behemoth, Night Terror, Corpse Engine).
- **Day/Night Cycle**: 180-second dynamic lighting cycle (Dawn, Day, Dusk, Night) with darkness masking and radial flashlight cone. Horde speed and aggression increase at night.
- **Maps**: 4 distinct sectors with environmental hazards (Abandoned Outpost, Toxic Marsh, Sunken Metro, Scorched Highway).

### B. Sanctuary Meta-Layer
- **Materials**: Scrap, Metal, Wood, Electronics.
- **Structures**: Command Bunker, Munitions Workshop, Reinforced Armory, Radar Array, Perimeter Palisade.
- **Permanent Upgrades**: Persistent health, damage, movement, magnet, and XP bonuses.
- **Survivors**: Recruited specialists granting passive perks.

### C. Base-Defense Raid Mode
- Real-time defense of the Sanctuary Core against marching boss hordes using placed auto-turrets, tesla pylons, and steel barricades.

---

## 🧪 How to Test & QA

1. **Combat Testing**:
   - Tap **"DEPLOY EXPEDITION (RUN)"** on the Main Menu.
   - Use the bottom virtual joystick to steer the player.
   - Weapons automatically lock onto the closest enemy in range.
   - Collect glowing gems to trigger the 3-choice level-up screen.
2. **QA Cheat Menu**:
   - Tap the orange **Bug** icon on the top right HUD to open the QA Panel.
   - Spawn +50 Crawlers or +150 stress horde.
   - Toggle God Mode, force immediate Level Up, or switch between Night and Dawn.
3. **Debug Overlay**:
   - Top-left HUD displays live FPS, frame step time (ms), active enemies count, active projectiles, and estimated draw calls.
4. **Sanctuary Meta-Testing**:
   - From Main Menu, tap **"ENTER SANCTUARY"**.
   - Review and upgrade structures, permanent attributes, or craft fuel packs.
5. **Unit Tests**:
   - Run `gradle :app:testDebugUnitTest` to execute the automated test suite for spatial hashing, economy math, and save migrations.
