# NIGHTWAKE - 3D Character & Enemy Specification Sheet

---

## 1. Playable Characters (Hero Class)

### A. Sarah Miller — "Dawn Vanguard"
- **Visual Description:** Former garrison officer in heavy tactical plate carrier with reactive neck collar, reinforced carbon fiber shoulder guards, combat helmet with glowing cyan HUD visor, and sling-mounted kinetic assault rifle.
- **Poly Budget (LOD 0 / LOD 1):** 5,200 tris / 2,100 tris.
- **Bone Count:** 32 (Standard Humanoid Rig).
- **Material IDs:** 1 (Hero_Atlas_01).
- **Emissive Elements:** Visor bar (Electric Cyan `#00E5FF`), battery level indicator on chest plate.
- **Signature Animations:** Crisp tactical sprint with low-ready rifle; military shoulder mount aim.

### B. Kaelen Chen — "Scavenger Ghost"
- **Visual Description:** Agile reconnaissance scout wearing lightweight composite hooded cowl, high-mobility exo-boots, scavenged thermal goggles (Amber neon `#FF9100`), and sawed-off shotgun holstered at thigh.
- **Poly Budget (LOD 0 / LOD 1):** 4,800 tris / 1,900 tris.
- **Bone Count:** 32.
- **Emissive Elements:** Double-lens goggle ring, wrist sensor pad.
- **Signature Animations:** Fluid low-center-of-gravity stride; rapid shotgun pump on reload.

### C. Dr. Elena Rostova — "Pyro Engineer"
- **Visual Description:** HAZMAT-reinforced technician suit with insulated rubberized joints, dual arc coils strapped to back generator rig, arc welding visor with purple tint, carrying an oversized energy arc projector.
- **Poly Budget (LOD 0 / LOD 1):** 5,500 tris / 2,400 tris.
- **Bone Count:** 34 (includes backpack coil antennas).
- **Emissive Elements:** Backpack battery coils (Neon Violet `#D500F9`), projector muzzle rings.

### D. Marcus Vance — "Trench Medic"
- **Visual Description:** Weathered field doctor with cross-strapped medical webbing, pneumatic injector canisters strapped across chest, gas mask with green filtered respirator, holding a pressurized corrosive sprayer.
- **Poly Budget (LOD 0 / LOD 1):** 5,100 tris / 2,200 tris.
- **Bone Count:** 32.
- **Emissive Elements:** Medical cross on helmet (Lime `#76FF03`), pressurized chemical canister gauge.

---

## 2. Horde Enemies (Swarm Class)

1. **Crawler:**
   - *Archetype:* 6-legged chitinous arachnid-infected crawler.
   - *Tris / Bones:* 950 tris / VAT (Vertex Animation Texture).
   - *Visual Feature:* Translucent pale pink flesh under obsidian carapace. Skitters violently along ground.

2. **Ash Shambler:**
   - *Archetype:* Asymmetrical burned biped walker.
   - *Tris / Bones:* 1,400 tris / VAT.
   - *Visual Feature:* Charcoal cracked skin with glowing amber embers inside ribcage.

3. **Bile Spitter:**
   - *Archetype:* Arched-neck ranged mutant.
   - *Tris / Bones:* 1,800 tris / VAT.
   - *Visual Feature:* Bulbous acid sac in throat that glows brilliant acidic green before vomiting projectiles.

4. **Garrison Brute:**
   - *Archetype:* Heavily mutated armored behemoth with fused riot armor plating.
   - *Tris / Bones:* 2,600 tris / 22 Bones.
   - *Visual Feature:* Massive spiked shoulder used to execute charging knockbacks.

5. **Swarm Leaper:**
   - *Archetype:* Agile ambush leaper with oversized muscular hind legs.
   - *Tris / Bones:* 1,200 tris / VAT.
   - *Visual Feature:* Coiled insectoid limbs, lunges 150 units in sudden bursts.

6. **Banshee Screamer:**
   - *Archetype:* Floating / gliding corrupted psionic husk.
   - *Tris / Bones:* 2,200 tris / 20 Bones.
   - *Visual Feature:* Ragged tattered shroud, jaw hinges unhinge 180 degrees to release purple shockwave.

7. **Shielded Enforcer:**
   - *Archetype:* Infected tactical SWAT officer with high-density riot shield.
   - *Tris / Bones:* 2,100 tris / VAT.
   - *Visual Feature:* Frontal ballistic shield blocks bullets until flank or high-pierce weapons shred it.

8. **Toxic Bloat:**
   - *Archetype:* Swollen necrotic gas-filled quadruped.
   - *Tris / Bones:* 1,600 tris / VAT.
   - *Visual Feature:* Pulsing pustules that erupt in green acid pool upon zero HP.

---

## 3. Boss Encounters

1. **Gorgon Behemoth (Minute 5 Boss):**
   - *Tris / Bones:* 12,500 tris / 48 Bones.
   - *Visual Feature:* 4-armed towering monstrosity with concrete-crushing club arms and cracked stone carapace.
   - *Telegraphed Moves:* Double ground stomp creating circular shockwaves, ground-cleaving charge.

2. **Night Terror (Minute 10 Boss):**
   - *Tris / Bones:* 14,000 tris / 56 Bones.
   - *Visual Feature:* Skeletal obsidian stalker wrapped in smoke tentacles, red optic clusters.
   - *Telegraphed Moves:* Shadow blink behind player, shadow-spike fan barrage.

3. **Corpse Engine (Minute 15 Final Boss):**
   - *Tris / Bones:* 16,500 tris / 64 Bones.
   - *Visual Feature:* Hybrid biomechanical excavator: tank treads on lower chassis, rusted hydraulic crane arms, furnace engine emitting black exhaust.
   - *Telegraphed Moves:* Heat-seeking missile pods, grinding saw arm sweep, summon elite crawler wave.
