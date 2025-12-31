# Changelog

## Main 1.2.20 - Ds 1.1.10 | 2025-12-31

### Changes:
- Now the update checker sends their messages as a single message to avoid being mixed with other messages.
- Exposed internal library manager as a workaround for the discord plugin. (sorry I didn't expect native paper to broke things!)

### Fixed:
- Ds: Fixed discord plugin not being able to load libraries.
- Ds: Fixed discord plugin incorrectly scheduling their tasks on paper.

## Main 1.2.19 - Ds 1.1.9 (No Changes) | 2025-12-28

### Fixed:
- Fixed registering async tab complete even if native brigadier is present. ([#21](https://github.com/NobelD/NobleWhitelist/issues/21))

## Main 1.2.18 - Ds 1.1.9 | 2025-12-26

### Added:
- Added native support to paper and folia.
- Added a bukkit command manager fallback in case it cannot load. (it seems since 1.19 bukkit/spigot users have been not able to execute commands)

### Changes:
- Cleaned up some code.
- Updated dependencies and modified some databases dependencies.
- Updated the update checker to show critical versions.

### Fixed:
- Fixed is whitelist function not being called. ([#20](https://github.com/NobelD/NobleWhitelist/issues/20))
- Use StringJoiner instead of StringBuilder to join strings. (lol)

## Main 1.2.17 - Ds 1.1.8 (No changes) | 2025-11-16

### Fixes:
- Fixed erroneous call when reading the arguments of a command.

## Main 1.2.16 - Ds 1.1.8 | 2025-11-15

### Changes:
- Now some special characters and symbols are allowed in commands. ([#19](https://github.com/NobelD/NobleWhitelist/issues/19))
- Some code cleanup related to splitting lines. 

### Fixes:
- Ds: Fixed some invalid special characters and symbols being available in commands.

## Main 1.2.15 - Ds 1.1.7 (No changes) | 2025-11-05

### Fixes:
- Fixed the optionals checking not being correctly processed at all.

## Main 1.2.14 - Ds 1.1.7 (No changes) | 2025-10-17

### Changes:
- Updated dependencies to support newer minecraft versions. ([#18](https://github.com/NobelD/NobleWhitelist/issues/18))

### Fixed:
- Fixed not correctly processing different names (name change)e.

## Main 1.2.13 - Ds 1.1.7 (No changes) | 2025-09-01

### Changes:
- Updated dependencies to support newer minecraft versions.

### Fixed:
- Fixed not correctly processing different names (name change) when the entry has the player uuid but a different name. ([#17](https://github.com/NobelD/NobleWhitelist/issues/17))

## Main 1.2.12 - Ds 1.1.7 | 2025-05-20

### Added:
- Added some overrides for the vanilla whitelist command, more options will come in 2.0

### Changes:
- Some code cleanup, specially to the join listener.
- Now the plugins and the discord integration properly runs in spigot, it also now supports bukkit. ([#16](https://github.com/NobelD/NobleWhitelist/issues/16))
- Now the plugin will not download the adventure library if it is running on a paper server.

## Main 1.2.11 - Ds 1.1.6 (No changes) | 2025-05-10

### Fixed:
- Modified the checking to properly check empty values. ([#15](https://github.com/NobelD/NobleWhitelist/issues/15))

## Main 1.2.10 - Ds 1.1.6 (No changes) | 2025-05-03

This version is not secure since the way it handles empty values allowed players to join if not registered, update to 1.2.11 for the fix.

### Fixed:
- Fixed checking not being able to properly compare some values and incorrectly checking empty values. ([#15](https://github.com/NobelD/NobleWhitelist/issues/15))

## Main 1.2.9 - Ds 1.1.6 (No changes) | 2025-02-28

### Changes:
- Added early value for the update checker when the plugin drops support for java 17.

### Fixed:
- Fixed incorrect values being used by the uuid and permission when using optional checking. ([#13](https://github.com/NobelD/NobleWhitelist/issues/13))

## Main 1.2.8 - Ds 1.1.6 | 2025-02-14

This versions ends support for 1.17 (which in most cases it was unable to run, requiring a greater java version, the plugin and some libraries did not work)

### Added:
- Added a support command to send useful information to the user.
- Added an unlink command to modify entries.

### Changes:
- End of support for minecraft 1.17, now it only supports 1.18+.
- Changed library resolver to an own fork to use a mirror repository.
- Expanded the update checker.
- Optimized internal libraries.
- Ds: Now some messages have better colors.
- Ds: Added more options to the send command.
- Ds: Modified link and unlink commands to be more precise.
- Ds: Now the emojis can also be defined with colons as in discord (Ex: `:cat2:`)

### Fixed:
- Fixed

## Main 1.2.7 - Ds 1.1.5 (No changes) | 2025-01-24

### Changes:
- Changed storage library to an own fork to handle better the files, this also should stop clearing files.

### Fixed:
- Fixed a setting typo.

## Main 1.2.6 - Ds 1.1.5 | 2025-01-22

This version was supposed to drop java 17, but it will have support until 2.0 releases.

### Added:
- Added extra storage settings to allow more setups and configuration.
- Ds: Added an experimental send command to send a message with a button to open a modal which allows users to register without using the command.

### Changes:
- Modified the update checker to check for some extra values.

## Main 1.2.5 - Ds 1.1.4 | 2024-11-05

### Changes:
- Some code cleanup, specially for the update checker.

### Fixed:
- Fixed sometimes checking options being skipped/not processed when checking if a player can join. ([#8](https://github.com/NobelD/NobleWhitelist/issues/8))
- Fixed incorrect config call when loading the fail action for the storage.
- Fixed in some rare cases where if a banned player or if the server is full, it will allow players to join if they meet the requirements.

## Main 1.2.4 - Ds 1.1.3 (No changes) | 2024-06-25

### Changes:
- Updated dependencies to support newer minecraft versions.
- Renamed placeholder expansions from "NWhitelist" to "nwhitelist".
- Now if the command manager cannot be initialized it will disable the commands and continue running instead of the plugin being disabled.

## Main 1.2.3 - Ds 1.1.3 | 2024-05-31

### Changes:
- Updated dependencies to support newer minecraft versions.
- Updated the update checker to be more precise.

## Main 1.2.2 - Ds 1.1.2 | 2024-05-23

### Added:
- Added new command to check if a player meets the inputted permission requirement.
- Added setting to skip saving the uuid of a player.
- Now is possible to add incomplete entries to the files and the plugin will fill them.

### Changes:
- Invalid entries will be silently ignored.
- Now it will show how much time took the libraries to load. 

### Fixed:
- Fixed config not being present when reloading a database. ([#5](https://github.com/NobelD/NobleWhitelist/issues/5))
- Now exceptions will correctly be logged.
- Ds: Fixed some placeholders.

## Main 1.2.1 - Ds 1.1.1 | 2024-03-21

### Fixed:
- Fixed storage exception handling.
- Ds: Fixed incorrect placeholder.
- Ds: Fixed provided messages using old placeholders.
- Ds: Fixed bot not requesting and saving the user cache.
- Ds: Fixed and changed how the bot gives the whitelist roles.
- Ds: Fixed miss-configured config option.

## Main 1.2.0 - Ds 1.1.0 | 2024-03-09

### Added:
- Added TOML as a storage type.
- Added new permission check.
- Added new commands to manage the checking and permissions.
- Ds: Added more detailed messages to errors and commands.
- Ds: Added list commands and updated account related commands.
- Ds: Added new commands to manage the checking and permissions.

### Changes:
- Refactored and divided code to support more platforms in the future.
- Now the plugin uses "me.nobeld.whitelist" for their code path instead of "me.nobeld.minecraft.whitelist".
- Updated dependencies, updated the library resolver to a fork to support newer java version.
- Now if the storage is unable to load it can run different actions instead of only closing the server.
- Now the reload command will also refresh the storage.
- Removed a placeholder and api call since it could generate an unwanted memory leak.
- Added more settings to the permission check, now it is possible to define comparable values.
- Ds: Now the plugin uses a library for the commands, this helps when handling errors and actions.
- Ds: Changed some message placeholders.

### Fixed:
- Fixed and modified some bypass checking.
- Fixed incorrect count for storage files. ?
- Ds: Fixed incorrect call to invalid uuid error message.
- Ds: Fixed everyone setting. ?

## Main 1.1.2 - Ds 1.0.2 | 2023-12-21

### Added:
- Added support for older versions.
- Added more api functions for the discord integration.
- Added more placeholders to the expansions.
- Ds: Added setting to use everyone as a role.
- Ds: Added sub roles to be added when the whitelist role is added.

### Changes:
- Cleaned some code related to the update checker and command manager.
- Now all the commands has their own permission.
- All the used permissions are now correctly defined.
- Added the "me" option to some commands to use the executor data.
- Now a message is sent when the libraries are loading.

### Fixed:
- Ds: Fixed invalid roles not being filtered.

## Main 1.1.1 - Ds 1.0.1 | 2023-11-12

### Added:
- Ds: Now the discord plugin sends messages when the discord bot is starting.
- Ds: Now the bot can set and remove roles when using commands to keep track of registered users.
- Ds: Now the plugin will send a message whenever an update is available.
- Ds: Added more placeholders for entries.

### Changes:
- Optimized some code related to the storage.
- Tweaked some messages and colors.
- Ds: If the base plugin is not load for any reason, now it will be indicated with a message.
- Ds: The plugin now sends formatted messages.
- Ds: Now sends more detailed messages when an error occurs.

### Fixed:
- Commands now correctly sends messages on non-paper servers.
- Fixed calls and versioning.

## Main 1.1.0 - Ds 1.0 | 2023-11-09

### Added:
- Added different types of storage types. (JSON, SQLite, MySQL, MariaDB)
- Added setting to close the server if the storage is unable to load.
- Created a discord integration to be able to connect and manage the whitelist from discord.
- Added setting to not get messages when an update is available.
- Added more events for different whitelist actions.
- Created expansion for the mini message placeholders.

### Changes:
- Optimized some code.
- The commands are now handled with a library using rich features, this provides better colors and arguments.
- The plugin libraries now need to be downloaded the first time is added to a server.
- Now the whitelist pass event is cancellable.

## 1.0.3 | 2023-10-27

### Added:
- Added settings to manage when a special char is found. ([#1](https://github.com/NobelD/NobleWhitelist/issues/1))
- Added settings to control the number of entries needed when using the list command to show reduced data.

### Changes:
- Tweaked some text and colors from messages and code.

### Fixed:
- Fixed not able to execute command with the plugin's permission. 

## 1.0.2 | 2023-10-16

### Changes:
- Now the console says the total amount ot stored entries.
- The list command shows reduced data for more than 12 entries, and it can only show up to 70 entries.
- The storage now removes invalid entries.

## 1.0.1 | 2023-10-16

### Added:
- Added setting to disallow the join when on rarely cases an entry is found for the name of a player but the uuid does not match.

### Changes:
- Renamed bypass placeholder from "by_pass" to "bypass".

### Fixed:
- Fixed formatting of some messages.
- Fixed adventure being closed before the close message is sent.

## 1.0.0 | 2023-10-15

- Initial release
