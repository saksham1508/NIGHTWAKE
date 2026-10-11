# NIGHTWAKE - Art Bible & Visual Design Guidelines
> *Core Fantasy: "Survive the night, fortify at dawn."*  
> *Aesthetic: Stylized Grim Dark-Sci-Fi / Gritty Tactical Military with High-Contrast Neon Highlights.*

---

## 1. Proportions & Silhouette Philosophy

- **Believable Heroic Proportions:** Human characters maintain a 7.5-head anatomical proportion. Avoid cartoonish bobblehead aesthetics or hyper-deformed caricatures. Silhouettes are widened strategically using tactical gear (plate carriers, backpack power cells, heavy weapon slings).
- **Silhouette Hierarchy for Instant Readability:**
  1. **Crawlers:** Low-profile, quadrupedal / arachnid silhouette crawling along ground level. Read at a glance as swift, squishy vermin.
  2. **Shamblers:** Upright, lurching, asymmetrical slouch with drooping arms. Represents standard bullet sponges.
  3. **Spitters:** Elongated arched necks with pulsing, distended glowing acid sacs located in the chest/throat.
  4. **Brutes:** Massive, wide, wedge-shaped upper torso and shoulder carapace with tiny legs. Visual weight communicates immense poise and knockback resistance.
  5. **Leapers:** Coiled, bent-knee praying-mantis silhouette ready to pounce.
  6. **Riot Enforcers:** Tall rectangular reinforced ballistic shield obstructing the front face.
  7. **Bosses (Gorgon / Corpse Engine):** Towering mechanical / mutated behemoths with 4x the silhouette volume of regular troops, sporting tiered glowing weakpoints.

---

## 2. Color Palette & Value Structure

| Tone | Hex Code | Purpose |
| :--- | :---: | :--- |
| **Abyssal Charcoal** | `#0D1117` | Base environment asphalt, unlit shadows, background tarmac |
| **Weathered Ballistic Metal** | `#263238` | Survivor armor plates, weapon receivers, steel barriers |
| **Toxic Sludge Ochre** | `#1B3322` | Marsh terrain, chemical hazard puddles, contaminated ground |
| **Neon Electric Cyan** | `#00E5FF` | Survivor visors, friendly energy shields, Tesla weapon arcs |
| **Tactical Amber / Flare** | `#FF9100` | Muzzle flashes, incendiary munitions, UI primary accents |
| **Apex Crimson Warning** | `#FF1744` | Infected weakpoints, boss warning telegraphs, night sirens |
| **Nanite Violet** | `#D500F9` | Evolved weapons, rare survivor blueprints, singularity saws |

---

## 3. Lighting & Neon Rim Lighting Rules

- **Key Light (Directional):** Simulates the moonlight / distant dawn sun. Soft, cool blue-grey tone (`#90CAF9`) angled from the north-west at 55° elevation.
- **Neon Rim Lighting (Emissive Fresnel):** Every character and enemy shader features a rim lighting calculation based on the surface normal and camera view vector. This produces a crisp 2-3 pixel high-contrast neon halo that pops entities against dark asphalt terrain even during pitch-black night phases.
- **Day/Night Cycle Lighting Parameters:**
  - **Dawn (0-20s):** Warm amber ambient fog (`#FFE082`), long soft shadows, full visibility.
  - **Day (20-80s):** Crisp neutral illumination (`#ECEFF1`), short contact shadows, 100% vision range.
  - **Dusk (80-100s):** Blood-orange gradient fading into purple twilight, fog rolls in from map edges.
  - **Night (100-180s):** Heavy indigo/black ambient darkness (`#050810`), vision cone restricted to the player's 3D volumetric flashlight beam; infected glowing eyes and neon visors intensify.

---

## 4. Material & Texture Guidelines

- **PBR Approximation for Mobile URP:**
  - Combined `Albedo + Roughness/Metallic` packed textures.
  - Metalness: 0.8 on ballistic firearms and armor rims; 0.0 on flesh and fabrics.
  - Roughness: Matte 0.7 on survivor fatigues; Glossy 0.2 on infected carapace and hazard puddles.
- **ASTC Compression:** ASTC 6x6 for general environment trim sheets and swarm atlases; ASTC 4x4 strictly reserved for hero and major boss textures.
- **Batching Mandate:** All modular environment props (sandbags, wrecked vehicles, jersey barriers) must share a single material atlas to enable full SRP Batcher optimization with minimal draw calls.
