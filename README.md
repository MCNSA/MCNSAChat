MCNSAChat
=========

Cross-server, multi-channel chat plugin.

# Commands

# Player commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/chelp`||Provides a help menu for all chat commands you can use.|
|`/c <channel>`|`mcnsachat3.channel`|Switches to a channel.|
|`/cmute <player>`|`mcnsachat3.mute`|Ignore player’s chat and private messages.|
|`/clist`||List available channels.|
|`/clisten <channel>`|`mcnsachat3.listen`|Allows to listen to more channels, while typing in another.|
|`/csearch <player>`|`mcnsachat3.search`|Views the channel a player is currently in.|
|`/list`||Lists everyone online. Aliases: `/who`, `/playerlist`, `/online`, `/players`|
|`/me <message>`||Emotes your message (will appear as `* you <message>`). Example: `/me needs more diamonds`|
|`/msg <player> <message>`|`mcnsachat3.msg`|Sends a private message to a player. Works across servers. Aliases: `/w`, `/tell`, `/whisper`|
|`/r <message>`|`mcnsachat3.msg`|Replies to the last person who messaged you (or whom you messaged)|
    
# Moderator commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/clock <player>`|`mcnsachat3.lock`|Locks a player in their channel, preventing them from switching.|
|`/cmove <player> <channel>`|`mcnsachat3.move`|Moves a player to a channel.|
|`/creconnect`|mcnsachat3.reconnect|Breaks the connection to the chat server, waiting for it to be restored later.|
|`/creload`|mcnsachat3.reload|Reloads configuration from file. Persistance isn’t reloaded (you need to reload the plugin or teh server).|
|`/ct <player> <time> <reason>`|`mcnsachat3.timeout`|Places player in timeout (they cannot chat or use `/msg`). Time format: <number of minutes>.|
|`/silence <player>`|`mcnsachat3.silence`|Toggles player’s silenced status (they won’t be able to chat at all).|

# Channel management

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/calias <channel> <alias>`|`mcnsachat3.alias`|Changes channel’s alias. Alias is a short command that can be used to send directly to that channel (example: `/m <message>`) or make the channel your primary channel to write in (example: `/m`).|
|`/ccolor <channel> <color>`|`mcnsachat3.color`|Changes channel name’s color. [List of color codes](http://www.minecraftwiki.net/wiki/Color_Codes). Example: `/ccolor mod b`.|
|`/cmode <channel> <mode>`|`mcnsachat3.mode`|Changes channel’s mode. Modes: `LOCAL` (makes channel work only on the current server), `MUTE` (nobody can talk), `RAVE` (random colors for letters, useful to stop huge fights), `RANDOM` (prepends everything with `&k` to make the text obfuscated), `LOUD` (enables bold and changes text to caps), `BORING` (removes all color and formatting), `PERSIST` (makes the channel show up in `/clist` even if it’s empty)|
|`/cname <channel> <name>`|`mcnsachat3.name`|Changes channel’s display name. Can be used to properly capitalize letters. Example: `/cname mod MOD`|
    
# MCNSA Fun Commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/dicks`||A message of love.|
|`/pong`||Alternative to `/ping`.|
|`/rand <min> <max>`||Gives a random number according to `min` (default: `0`) and `max`.|
|`/ranks`||Lists all of the server ranks.|

# Configuration

## Permissions

|**Permission Node**|**Description**|
|:------|:----------|
|`mcnsachat3.read`|Allows players to read chat.|
|`mcnsachat3.write`|Allows players to chat.|
|`mcnsachat3.read.<name>`|Allows players to chat in a special channel. Permission name is declared in `persistence.yml`, via `read_permission`.|
|`mcnsachat3.write.<name>`|Allows players to chat in a special channel. Permission name is declared in `persistence.yml`, via `write_permission`.|
|`mcnsachat3.forcelisten.<name>`|Force players to listen to a channel (example: useful for moderator channel, so mods don’t have to explicitly do `/clisten mod`). Permission name is declared in `persistence.yml`, via `read_permission`.|
|`mcnsachat3.user.cancolor`|Allows to use color (ie: `&c`) and formatting (ie: `&o`) codes in chat. Cannot overwrite `/cmode <channel> BORING`.|
