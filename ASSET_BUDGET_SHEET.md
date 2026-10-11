# NIGHTWAKE - 3D Asset Budget Sheet (Target Baseline)
> *Platform: Android (Adreno 610 / Snapdragon 680, 3-4GB RAM, 60 FPS Target)*  
> *Note: All budget figures represent conservative engineering assumptions to be validated via device profiling.*

---

## 1. Triangle, Bone & Texture Budgets per Class

| Asset Class | Max Tris (LOD 0) | Max Tris (LOD 1) | Max Tris (LOD 2) | Max Bones | Texture Resolution | Max Draw Calls | Animation Clips |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Playable Hero** | 4,500 - 6,000 | 2,200 | 1,000 | 32 (Humanoid) | 1x 1024x1024 Atlas (ASTC 6x6) | 1 - 2 | 8 clips (Idle, Run, Aim, Fire, Roll, Hit, Die, Stun) |
| **Horde Tier 1 (Crawler, Leaper)** | 800 - 1,200 | 450 | 180 (Billboard) | VAT / 12 Bones | Shared 1024x1024 Swarm Atlas | 1 (GPU Instanced) | 4 clips (Run, Attack, Hit, Death) |
| **Horde Tier 2 (Shambler, Bloat)** | 1,200 - 1,800 | 700 | 250 | VAT / 16 Bones | Shared 1024x1024 Swarm Atlas | 1 (GPU Instanced) | 4 clips (Walk, Melee, Hit, Explode) |
| **Ranged Hostile (Spitter, Riot)** | 1,800 - 2,400 | 900 | 350 | VAT / 18 Bones | Shared 1024x1024 Swarm Atlas | 1 (GPU Instanced) | 5 clips (Walk, Aim, Shoot, Shield, Death) |
| **Elite / Screamer** | 2,500 - 3,200 | 1,200 | 450 | 24 Bones | 512x512 Unique Atlas | 1 - 2 | 6 clips (Walk, Screech, Buff, Hit, Death) |
| **Major Boss (Gorgon, Corpse Engine)** | 12,000 - 16,000 | 6,500 | 2,800 | 48 - 64 Bones | 2048x2048 Atlas (ASTC 4x4) | 2 - 3 | 12 clips (Intro, Idle, Stomp, Charge, Barrage, Enrage, Death) |
| **Sanctuary Survivor NPCs** | 2,800 - 3,500 | 1,400 | - | 28 (Humanoid) | Shared 1024x1024 NPC Atlas | 1 | 4 clips (Idle, Work, Repair, Salute) |
| **Sanctuary Buildings (Tiers 1-5)** | 3,000 - 5,000 | 1,500 | - | Static (0) | Shared 2048x2048 Trim Sheet | 1 per building | Static / 1 Deploy FX |
| **Environment Kits (Per 10m Tile)** | 800 - 1,500 | 400 | - | Static (0) | Shared 2048x2048 Kit Atlas | SRP Batcher merged | Static |
| **Projectiles & Pickups** | 80 - 250 | - | - | Static (0) | 256x256 VFX Atlas | Instanced | Rotating / Pulse |

---

## 2. Animation & Rendering Pipeline Decision: Vertex Animation Textures (VAT) vs. GPU Instancing
- **Horde Enemies (150+ units):** We evaluate and select **Vertex Animation Textures (VAT)** with GPU instancing for all standard swarm units (Crawlers, Shamblers, Spitters).
  - *Rationale:* Standard SkinnedMeshRenderers with 150+ units cause massive CPU bone transformation matrix recalculations and severe draw call overhead. VAT encodes mesh positions and normals into texture float buffers sampled directly in vertex shaders, reducing CPU draw calls to a single instanced draw batch per mesh archetype and maintaining **zero per-frame CPU skinning overhead**.
- **Player, Bosses & Close Elites:** Utilize standard **SkinnedMeshRenderer** with full blend trees, aiming IK, and ragdoll/hit-react blend nodes.

---

## 3. Package Size & Memory Budget (150MB Play Asset Limit)

- **Total Base APK/AAB Budget:** < 140MB (Safe margin below 150MB Google Play threshold).
  - Shaders & Code: ~8MB
  - Audio (Ogg Vorbis compressed, mono SFX, stereo music): ~22MB
  - Textures (ASTC 6x6 / ASTC 4x4 for bosses): ~55MB
  - 3D Meshes & Animation Clips (VAT + Humanoid FBX): ~35MB
  - UI, Fonts & Meta configs: ~15MB
- **Peak Runtime RAM Budget:** < 1.1GB allocated, well within 3-4GB physical RAM on Snapdragon 600-series devices without low-memory kills.
