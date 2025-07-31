<h1 align="center">
  <img src="https://cdn.modrinth.com/data/jtmvUHXj/14fabf4859e845b0bd6659daf2375be3e88f59ec.png" width=230>
  <br>
  αccessories Compatibility Layer
  <br>
  <a href="https://modrinth.com/mod/accessories-compatibility-layer/">
      <img src="https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=1bd96a&labelWidth=15&logo=modrinth&logoColor=white">
  </a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/accessories-compatibility-layer">
      <img src="https://img.shields.io/badge/-curseforge-gray?style=for-the-badge&labelColor=f16436&labelWidth=15&logo=curseforge&logoColor=white">
  </a>
  <br>
  <a href="https://discord.gg/xrwHKktV2d">
      <img src="https://img.shields.io/discord/825828008644313089?label=wisp%20forest&logo=discord&logoColor=white&style=for-the-badge">
  </a>
  <a href="https://docs.wispforest.io/accessories/home/">
    <img src="https://img.shields.io/badge/Documentation-Link-SECRET_MESSAGE?link=https%3A%2F%2Fdocs.wispforest.io%2Faccessories%2Fhome%2F&logo=materialformkdocs&logoColor=white&color=blue&style=for-the-badge">
  </a>
</h1>

## [WIP] Accessories Compatibility Layer

Using Accessories as the framework, this mod acts as a wrapper for both Trinkets and Curios, using mixin patches on top of both mods. It is designed with support for a Connector or Kilt, meaning trinkets or curios support will work on fabric or neoforge.

### Requires: 
- [Accessories](https://modrinth.com/mod/accessories/)
- [Trinkets](https://modrinth.com/mod/trinkets) and/or [Curios](https://modrinth.com/mod/curios)

### Replaces: 
The following compat layers are **no longer** when using this version:
- [Trinkets Compat Layer](https://modrinth.com/mod/accessories-tc-layer)
- [Curios Compat Layer](https://modrinth.com/mod/accessories-cc-layer)

### Issues:
- All screens or renderings for such have been unified to the Accessories Screen for the time being
- Curios: Some features are know problems and will be attempted to be resolved within the future
  - Active states are currently unimplemented, meaning mods taking advantage of such will be unable to control state of equipped accessories
  - `ICurioSlotExtension` is not implemented, meaning any stack replacement or tooltip adjustments will not work
