# Noble Whitelist
This plugin is a simple way to manage the whitelist, using name, uuid or permission.

Also, you can place if you want to make some of these as optional, required or disabled.

## Links:

- **[GitHub Repository](https://github.com/NobelD/NobleWhitelist)**
- **[Modrinth Page](https://modrinth.com/plugin/noble-whitelist)**
- **[Spigot Page](https://www.spigotmc.org/resources/noble-whitelist.113107/)**
- **[Hangar Page](https://hangar.papermc.io/NobelD/NobleWhitelist)**

## Versions:
- This plugin support all spigot base software (paper is recommended)
- This plugin only support versions from 1.17+

## Commands:

**Base command:** /nwl `(alias: noblewl, nwhitelist)`

- `/nwl add name <name>` Adds to the whitelist using their name.
- `/nwl add uuid <uuid>` Adds to the whitelist using their uuid.
- `/nwl add allOnline` Adds all the players online to the whitelist.
- `/nwl remove name <name>` Removes of the whitelist using their name.
- `/nwl remove uuid <uuid>` Removes of the whitelist using their uuid.
- `/nwl list` Sends you a list of all the players `(name : uuid)`
- `/nwl on` Enables the whitelist.
- `/nwl off` Disables the whitelist.
- `/nwl reload` Reloads all the data, its only necessary if editing the whitelist file.
- `/nwl forceclear` Clears all the players from the whitelist.

## Permissions:
- `noblewhitelist.admin` Lets you use all the commands.
- `noblewhitelist.bypass` Permits by pass the whitelist if enabled.

## Placeholders:
This plugin has compatibility with **[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)** and you can use some placeholders.

- `%NWhistelist_whitelist_active%` **yes** if the whitelist is enabled, otherwise return **no**.
- `%NWhistelist_join_type%` Returns how the player joined the server.
  - **none** If the player is not whitelisted.
  - **bypass** If only has the bypass permission.
  - **name** If only the name is registered.
  - **uuid** If only the uuid is registered.
  - **normal** If the name and uuid are registered
  - **all** If their name, uuid are registered and can bypass.
- `%NWhistelist_is_whitelisted%` **yes** if the player can join by name or uuid, otherwise **no**.
- `%NWhistelist_by_pass%` **yes** if the player can bypass the whitelist, otherwise **no**.
- `%NWhistelist_optional_join%` **yes** if the player can join by their name or uuid, otherwise **no**.
- `%NWhistelist_can_pass%` The same of optional join, but applying the respective checking (disabled, optional or required)