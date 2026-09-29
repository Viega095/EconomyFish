# 🐟 FishingEconomy - Modern Economy, Stock Market & Maritime Plugin

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org)
[![Build](https://img.shields.io/badge/Build-Maven-orange.svg)](https://maven.apache.org)

FishingEconomy is an enterprise-grade Minecraft economy ecosystem combining complete financial markets (Stock Market, Player Corporations, Treasury Bonds, Real Estate) with deep interactive maritime gameplay (Action Bar Reeling minigame, Offhand Baits, Genetic Fish Breeding, Oceanic Expeditions, and Kraken World Bosses).

---

## 🌟 Key Features

* **Interactive Fishing Reeling**: Dynamic tension HUD on the action bar `[||||||||||]` with line-break risk.
* **Player Corporations (`/corp`)**: Start companies, issue Initial Public Offerings (IPO), trade shares, and pay dividends.
* **Candlestick Map Charts (`/mapchart`)**: In-game visual Japanese Candlestick stock charts rendered on physical Minecraft maps (`MapRenderer`) to hang in offices or trading floors.
* **Automated Fishing Tournaments (`/ftournament`)**: Scheduled server-wide fishing derbies with live leaderboards and prize distributions.
* **Aquaculture & Genetic Breeding (`/breedfish`)**: Placeable fish tanks where players breed rare fish with genetic mutations for up to +500% sell value.
* **Oceanic Expeditions & Kraken Boss (`/expedition`)**: Sail to the Abyssal Trench to battle the Mythic Kraken for legendary artifacts.
* **Treasury Bonds & Credit Scoring (`/bonds`)**: Fixed-yield investments (1h, 24h, 7d) and dynamic credit rating (300 to 850 pts).
* **Commercial Real Estate (`/property`)**: Buy and rent commercial stalls for passive recurring income.
* **Modular Custom Rods (`/customrod`)**: Forged rods (*Leviathan Bane*, *Magma Fisher*, *Siren Weaver*) with custom attributes.

---

## 📜 Commands

| Command | Description |
| :--- | :--- |
| `/balance` / `/pay` / `/baltop` | Standard economy operations |
| `/corp <create\|invest\|dividend\|list>` | Corporation and stock management |
| `/mapchart [symbol]` | Receive a live stock chart map item |
| `/ftournament <status\|start\|end>` | Manage fishing tournaments |
| `/breedfish` | Breed fish in main and offhand |
| `/merchant <list\|buy>` | Black market ocean merchant ship |
| `/expedition start` | Embark on a deep sea expedition |
| `/bonds <buy\|claim>` | Treasury bonds and credit rating |
| `/property <list\|buy>` | Commercial stalls and real estate |
| `/customrod <list\|buy>` | Forge mythic modular fishing rods |

---

## 🔨 Building from Source

```bash
git clone <REPO_URL>
cd EconomyFish
mvn clean package
```
Output JAR will be in `target/FishingEconomy-1.0-SNAPSHOT.jar`.
