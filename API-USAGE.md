# RavenSMP API Usage Guide

Welcome to the **RavenSMP** API documentation. This guide details how to integrate your custom plugins with RavenSMP to
look up account data, manage teams, handle localizations, and listen to team-related gameplay events and more.

## ⚡ Accessing the API

To access the API instance, fetch the static singleton:

```java
RavenAPI api = RavenAPI.api();
```

## 🔌 Core Adaptors
---

The API is segmented into three specialized adaptors accessible through `RavenAPI.api()`:

| Method Name         | Description                                                                                          |
|---------------------|------------------------------------------------------------------------------------------------------|
| `languageAdaptor()` | Manages player locale profiles or fetches specific language configurations by ID.                    |
| `accountAdaptor()`  | Used to look up player account profiles or trace online player records.                              |
| `teamAdaptor()`     | Provides methods to fetch `RavenSMPTeam` instances using unique team identifiers or a player's UUID. |

### 🌐 Language Adaptor

| Method Name                    | Description                                                                                          |
|--------------------------------|------------------------------------------------------------------------------------------------------|
| `findLanguageById(String)`     | Finds a cached `RavenLanguage` by the provided ID. Returns `null` if it's invalid or does not exist. |
| `findPlayerLanguageById(UUID)` | Finds a cached `RavenLanguage` of the player, returns plugin's default language if does not exist.   |

### 👥 Account Adaptor

| Method Name                   | Description                                                                                       |
|-------------------------------|---------------------------------------------------------------------------------------------------|
| `findAccountById(UUID)`       | Finds a `RavenAccount` by the provided ID from the database. Returns `null` if it does not exist. |
| `findOnlineAccountById(UUID)` | Finds a cached `RavenAccount` from the player's unique ID, returns `null` if it does not exist.   |
| `onlineAccounts()`            | Gets an immutable collection of `RavenAccount`.                                                   |

### 🛡️ Team Adaptor

| Method Name                | Description                                                                                     |
|----------------------------|-------------------------------------------------------------------------------------------------|
| `findTeamById(String)`     | Finds a `RavenSMPTeam` by the provided team ID. Returns `null` if it does not exist.            |
| `findTeamByPlayerId(UUID)` | Finds a cached `RavenSMPTeam` from the player's unique ID, returns `null` if it does not exist. |

## 🔔 Events API

---

RavenSMP fires robust, specialized events through the Bukkit Event Architecture. Cancellable events extend
`RavenCancellableEvent` and can be caught at early lifecycle stages (prefixed with `Pre`).

### Available Events

All properties expose fluent-style access methods (e.g., call `event.team()` instead of `event.getTeam()`).

| Event Class Name                         | Description                                                                | Fluent Accessors                               |
|:-----------------------------------------|:---------------------------------------------------------------------------|:-----------------------------------------------|
| `RavenSMPTeamCreateEvent`                | Fired right after a team is established.                                   | `player()`, `team()`                           |
| `RavenSMPTeamMemberPreJoinEvent`         | **Cancellable.** Fired before a new player completes joining a team.       | `whoJoined()`, `host()`, `team()`              |
| `RavenSMPTeamMemberPreKickEvent`         | **Cancellable.** Fired before a team member is ejected by authority.       | `whoKicked()`, `kickedMember()`, `team()`      |
| `RavenSMPTeamMemberPreLeaveEvent`        | **Cancellable.** Fired before a team member voluntarily departs.           | `whoLeft()`, `team()`                          |
| `RavenSMPTeamPreDisbandEvent`            | **Cancellable.** Fired prior to a team being broken up and deleted.        | `whoDisbanded()`, `team()`                     |
| `RavenSMPTeamPreTransferLeadershipEvent` | **Cancellable.** Fired before the team ownership/leadership changes hands. | `oldTeamLeader()`, `newTeamLeader()`, `team()` |

### Listening to Events Example

```java
import ir.ariwuh.plugin.ravensmp.api.event.team.RavenSMPTeamMemberPreJoinEvent;
import ir.ariwuh.plugin.ravensmp.api.event.team.RavenSMPTeamPreDisbandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TeamLifecycleListener implements Listener {

    @EventHandler
    public void onPreJoin(RavenSMPTeamMemberPreJoinEvent event) {
        Player player = event.whoJoined();
        RavenSMPTeam team = event.team();
        
        // Example logic: Prevent joining if the player's game-mode is creative.
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            player.sendRichMessage("<red>You are not allowed to join teams in creative mode.");
        }
    }

    @EventHandler
    public void onPreDisband(RavenSMPTeamPreDisbandEvent event) {
        RavenSMPTeamMember whoDisbanded = event.whoDisbanded();
        // Disband restrictions or broadcast updates can be managed here
    }
}
```