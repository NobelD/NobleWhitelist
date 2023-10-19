# Noble Whitelist
This plugin is a simple way to manage the whitelist, using name, uuid or permission.

Also, you can modify if you want to make some of these as optional, required or disabled.

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

- `/nwl add name <name>` Adds the name to the whitelist.
- `/nwl add uuid <uuid>` Adds the uuid to the whitelist.
- `/nwl add allOnline` Adds all the players online to the whitelist.
- `/nwl remove name <name>` Removes the name of the whitelist.
- `/nwl remove uuid <uuid>` Removes the uuid of the whitelist.
- `/nwl list` Sends you a list of all the whitelisted players `(name : uuid)`
- `/nwl on` Enables the whitelist.
- `/nwl off` Disables the whitelist.
- `/nwl reload` Reloads all the data, its not neccesary unless the whitelist file is modified.
- `/nwl forceclear` Clears all the players from the whitelist and he whitelist file.

## Permissions:
- `noblewhitelist.admin` Lets you use all the commands.
- `noblewhitelist.bypass` Permits bypass the whitelist if enabled and their check enabled.

## Placeholders:
This plugin has compatibility with **[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)** and you can use some placeholders.

- `%NWhistelist_whitelist_active%` **yes** if the whitelist is enabled, otherwise return **no**.
- `%NWhistelist_join_type%` Returns how the player joined the server.
  - **none -** If the player is not whitelisted.
  - **bypass -** If only has the bypass permission.
  - **name -** If only the name is registered.
  - **uuid -** If only the uuid is registered.
  - **normal -** If the name and uuid are registered
  - **all -** If their name, uuid are registered and can bypass.
- `%NWhistelist_is_whitelisted%` **yes** if the player can join by name or uuid, otherwise **no**.
- `%NWhistelist_bypass%` **yes** if the player can bypass the whitelist, otherwise **no**.
- `%NWhistelist_optional_join%` **yes** if the player can join by their name or uuid, otherwise **no**.
- `%NWhistelist_can_pass%` The same of optional join, but applying the respective checking (disabled, optional or required)
