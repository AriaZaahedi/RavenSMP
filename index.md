# RavenSMP Codebase Index

## Overview
RavenSMP is a lightweight PaperMC plugin for Minecraft survival servers, focusing on team management with ultra-low resource usage. The codebase uses MongoDB for data persistence and follows clean architecture principles.

---

## Directory Structure

```
src/main/java/ir/ariwuh/plugin/ravensmp/
├── RavenSMPPlugin.java           # Main plugin entry point
├── RavenSMPPluginLoader.java     # Plugin loader
├── api/                          # Public API interfaces
│   ├── account/                  # Account-related APIs
│   ├── adaptor/                  # Adaptor interfaces for external access
│   ├── event/                    # Custom event classes
│       └── team/                 # Team-specific events
│   ├── language/                 # Language system APIs
│   └── team/                     # Team API interfaces
├── command/                      # Command implementations
│   ├── api/                      # Command API base classes
│   └── subcommand/               # Subcommand implementations
│       ├── settings/             # Settings commands
│       ├── smp/                  # SMP-level commands
│       └── team/                 # Team management commands
├── config/                       # Configuration handling
├── database/                     # Database access layer
│   ├── codec/                    # MongoDB codecs
│   └── dao/                      # Data Access Objects
├── impl/                         # API implementations
├── listener/                     # Event listeners
│   ├── ChatListener.java        # Chat formatting
│   └── PlayerListener.java      # Player join/quit events
├── manager/                      # Core managers
│   ├── AccountManager.java      # Account lifecycle management
│   ├── DatabaseManager.java     # MongoDB connection pool
│   ├── LanguageManager.java     # Language system
│   ├── PluginSettingsManager.java # Settings handling
│   └── WorldManager.java        # World management
├── manager/team/                 # Team-specific managers
│   ├── TeamInvitationManager.java    # Invitation handling
│   ├── TeamManager.java              # Core team operations
│   ├── TeamOptionsManager.java       # Team options
│   └── TeamTagManager.java           # Scoreboard/team tags
├── model/                        # Data models
│   ├── account/                  # Account models
│   └── team/                     # Team models
│       └── status/               # Team status enums
├── task/                         # Scheduled tasks
└── utility/                      # Utility classes
```

---

## Key Components

### Core Plugin (`RavenSMPPlugin.java`)
- **Role**: Main plugin initialization and shutdown
- **Key Responsibilities**:
  - Initialize all managers on `onEnable()`
  - Cleanup resources on `onDisable()`
  - Register listeners and commands
- **Optimization Notes**:
  - Uses lazy initialization for managers
  - Properly nulls out static references

### Database Layer (`DatabaseManager.java`, `*_Dao.java`)
- **Architecture**: MongoDB-based persistence
- **Key Files**:
  - `DatabaseManager.java`: Connection pool management
  - `SMPAccountDao.java`: Account CRUD operations
  - `SMPTeamDao.java`: Team CRUD operations
  - `SMPTeamOptionsDao.java`: Team options storage
- **Optimization Opportunities**:
  - Consider connection pooling configuration
  - Index usage on frequently queried fields

### Team Management (`TeamManager.java`)
- **Role**: Core team creation, disbanding, member management
- **Key Operations**:
  - `createTeam()`: Creates new teams with cooldown checks
  - `disbandTeam()`: Removes teams and broadcasts messages
  - `leaveTeam()`: Player leaves their team
  - `kickMember()`: Leader kicks a team member
  - `transferTeamLeadership()`: Transfers leadership
  - `teleportToTeamHome()`: Asynchronous teleportation
- **Optimization Issues Fixed**:
  - ✅ **Line 40-41**: Added `ConcurrentHashMap<UUID, SMPTeam>` for O(1) team lookups by player ID
  - ✅ **Line 46-56**: Build index on loadTeams() to index all team members
  - ✅ **Line 93-94**: Index new teams when created
  - ✅ **Line 133-141**: Rebuild index when team disbands
  - ✅ **Line 183-190**: Rebuild index when player leaves
  - ✅ **Line 248-255**: Rebuild index when member is kicked
  - ✅ **Line 305-306**: Update index on leadership transfer
  - ✅ **Line 334-337**: O(1) lookup in findTeamByPlayerId()
    ```java
    public @Nullable SMPTeam findTeamByPlayerId(@NotNull UUID playerId) {
        return teamsByPlayerId.get(playerId); // O(1) instead of O(n)
    }
    ```
  - ✅ **Line 348-357**: Added `rebuildIndex()` helper method for consistency

### Account Management (`AccountManager.java`)
- **Role**: Track online accounts and handle player joins/quits
- **Data Structure**: `ConcurrentHashMap<UUID, SMPAccount>`
- **Optimization**: Already uses ConcurrentHashMap for thread safety

### Team Invitation System (`TeamInvitationManager.java`)
- **Role**: Handle team invitations with expiration
- **Key Issues Fixed**:
  - ✅ **Line 46**: Changed from `HashMap` to `ConcurrentHashMap` for thread-safe operations

### Utility Classes

#### `TimedHashSet.java` - Optimized
- **Role**: Time-based expiration tracking (e.g., teleport cooldowns)
- **Optimizations Applied**:
  - ✅ Changed from `HashMap<K, Instant>` to `TreeMap<Instant, K>` for O(log n) cleanup
  - ✅ Store expiration time as key for efficient range-based cleanup
  - ✅ Use `headSet().clear()` instead of `removeIf()` for bulk removal
- **Before**: O(n) linear scan on every cleanup cycle
- **After**: O(log n) TreeMap operations + O(1) bulk clear

#### `RavenUtility.java`
- **Role**: Async task scheduling and event calling
- **Optimization**: Uses Bukkit's async scheduler correctly

---

## Performance Optimization Summary

### Critical Issues Fixed:

1. **Team Lookup Performance** (`TeamManager.findTeamByPlayerId()`):
   - **Problem**: O(n) lookup using stream filter on HashSet
   - **Fix**: Added `ConcurrentHashMap<UUID, SMPTeam>` for O(1) lookups
   - **Impact**: Every player/team operation now performs in constant time regardless of team count

2. **Thread Safety** (`TeamInvitationManager`):
   - **Problem**: Non-concurrent HashMap for pending invitations
   - **Fix**: Changed to `ConcurrentHashMap`
   - **Impact**: Eliminates race conditions in multi-player environments

3. **TimedHashSet Cleanup Efficiency**:
   - **Problem**: Linear scan of entire map on every cleanup cycle
   - **Fix**: Use TreeMap with expiration time as key, O(log n) cleanup
   - **Impact**: Significantly faster cooldown tracking as server grows

### Additional Improvements:

4. **Index Rebuilding**:
   - Added `rebuildIndex()` method to maintain consistency after team changes
   - Ensures lookup performance remains optimal

5. **Missing Method Added**:
   - Added `updateTeamMemberUsername()` which was missing but called by PlayerListener

---

## API Structure

### RavenAPI (Abstract Base)
- Provides typed accessors for:
  - `languageAdaptor()`: Language system access
  - `accountAdaptor()`: Account operations
  - `teamAdaptor()`: Team operations

### Adaptors
- `RavenAccountAdaptor`: Account-related operations
- `RavenLanguageAdaptor`: Language/translation operations  
- `RavenTeamAdaptor`: Team-related operations

---

## Configuration (`PluginSettings.java`)

### Database:
- `databaseUri`: MongoDB connection string
- `databaseName`: Database name

### General:
- `defaultNameTagColor`: Default color for name tags
- `globalChatFormat`: Chat message format

### Team Settings:
- `allowedTeamIdRegex`: Regex for valid team IDs
- `maxTeamIdLength`: Maximum team ID length
- `maxTeamTagLength`: Maximum team tag length
- `maxTeamMember`: Maximum members per team (default: 6)
- `teamCreationCooldownTimeSeconds`: Cooldown between team creations
- `teamInviteExpirationTimeSeconds`: Invitation expiration time
- `teamHomeTeleportCooldownTimeSeconds`: Teleport cooldown
- `blacklistedTeamHomeWorlds`: Worlds where team homes are not allowed

---

## Event System

### Custom Events:
- `RavenSMPTeamCreateEvent`: Fires on team creation
- `RavenSMPTeamMemberPreJoinEvent`: Fires before member joins
- `RavenSMPTeamMemberPreKickEvent`: Fires before member kick
- `RavenSMPTeamMemberPreLeaveEvent`: Fires before member leaves
- `RavenSMPTeamPreDisbandEvent`: Fires before team disband
- `RavenSMPTeamPreTransferLeadershipEvent`: Fires before leadership transfer

---

## Build Instructions

```bash
# Prerequisites: JDK 25, Gradle
./gradlew build

# Output location: build/libs/
```

---

## License
GPL-3.0
