# RavenSMP

---
## ❓What is Raven SMP?
**RavenSMP** is a lightweight, super-optimized PaperMC plugin built to give your survival server a smooth upgrade without dragging down performance. The codebase is kept clean, modern, and frequently updated so it just works seamlessly with the latest Minecraft versions. The focus here is simple: ultra-low resource usage and zero unnecessary lag, keeping your server's TPS right where it belongs.

Instead of forcing bloated mechanics on your players, RavenSMP respects the vanilla game while sprinkling in the essential quality-of-life tools every community actually wants. You get straight-to-the-point features like a solid team system, quick PvP toggles, and portal controls. It’s the perfect backbone for an SMP that wants to stay close to vanilla but feel a lot smoother to play.
## 📦 Installation

---

1. Download the latest `RavenSMP-xxx.jar` from the [Releases](https://github.com/AriaZaahedi/RavenSMP/releases) page.
2. Drag and drop the downloaded `.jar` file into your server's `plugins/` directory.
3. Restart your server.
4. Configure the plugin to your liking via the `plugins/RavenSMP/settings.yml` file.

## ⚙️ Compatibility

---

- **Minecraft Versions**: 26.1.x or higher (PaperMC or fork of it. **Spigot is NOT supported.**)
- **Java Version**: Java 25 or higher
- **Mongo Database**: Latest version if possible.

## 🛠️ Commands & Permissions

---

### 🛡️ Team Commands

| Command                   | Description                       |
|---------------------------|-----------------------------------|
| `/team create <teamId>`   | Create a team.                    |
| `/team disband <teamId>`  | Disband your team.                |
| `/team kick <player>`     | Kick a team member.               |
| `/team leave`             | Leave the team.                   |
| `/team home`              | Teleport to team home.            |
| `/team chat <message>`    | Send a message to your teammates. |
| `/team transfer <player>` | Change the team leadership.       |
| `/team invite <player>`   | Invite a player.                  |
| `/team accept <teamId>`   | Accept a team invitation.         |
| `/team decline <teamId>`  | Decline a team invitation.        |

### 🔧 Team Option Commands

| Command                                       | Description                        |
|-----------------------------------------------|------------------------------------|
| `/team options tagName <tagName>`             | Edit the team tag name.            |
| `/team options tagColor <color>`              | Adjust the team tag color.         |
| `/team options friendlyFire <true\|false>`    | Toggle friendly fire for the team. |
| `/team options chatMuted <true\|false>`       | Toggle team chat.                  |
| `/team options setHome <my-location\|remove>` | Set or remove team home.           |

### 🧩 Other Commands

| Command                       | Description                         |
|-------------------------------|-------------------------------------|
| `/settings toggleTeamInvites` | Toggle team invitation.             |
| `/language <languageId>`      | Change the language for yourself.   |
| `/smp reload`                 | Reload plugin config and languages. |

---
## 🌐 How to add a custom language

1. Open `plugins/RavenSMP/languages/` folder.
2. Copy `english.yml` and rename it the language you want to add. i.e. `french.yml`
3. Edit `language-settings.display-name` and `language-settings.default` values (Must not be same as others).
4. Use `/smp reload` to apply the new language configurations.

## 🔌 API Usage

RavenSMP provides a simple API for other plugins to hook into. You can find the instructions and how to
use [here](https://github.com/AriaZaahedi/RavenSMP/blob/master/API-USAGE.md).

## 🛠️ Building from source

To compile RavenSMP, you need JDK 25 and an internet connection.
1. Clone this repository.
2. Run `./gradlew build` from your terminal.
3. Compiled jar file can be found in `build/libs/` directory.