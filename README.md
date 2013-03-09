MCNSAChat
=========

Cross-server, multi-channel chat plugin.

# Player commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|/chelp||Provides a help menu for all chat commands you can use|
|/c \<channel\>||Switches to a channel.|
|/cmute \<player\>||Ignore player’s chat and private messages.|
|/clist||List available channels.|
|/clisten \<channel\>||Allows to listen to more channels, while typing in another.|
|/csearch \<player\>||Views the channel a player is currently in.|
|/list||Lists everyone online. Aliases: `/who`, `/playerlist`, `/online`, `/players`|
|/me \<message\>||Emotes your message (will appear as `* you \<message\>`). Example: `/me needs more diamonds`|
|/msg \<player\> \<message\>||Sends a private message to a player. Works across servers. Aliases: `/w`, `/tell`, `/whisper`|
|/r \<message\>||Replies to the last person who messaged you (or whom you messaged)|
# Mod commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|/clock \<player\>||Locks a player in their channel, preventing them from switching.|
|/cmove \<player\> \<channel\>||Moves a player to a channel.|
|/creconnect||Breaks the connection to the chat server, waiting for it to be restored later.|
|/creload||Reloads configuration from file. Persistance isn’t reloaded (you need to reload the plugin or teh server).|
|/timeout \<player\> \<time\> \<Reason\>||Places player in timeout (they cannot chat or use `/msg`). Time format: \<number of minutes\>.|
|/silence \<player\>||Toggles player’s silenced status (they won’t be able to chat at all).|

# Channel management

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|/calias \<channel\> \<alias\>||Changes channel’s alias. Alias is a short command that can be used to send directly to that channel (example: `/m \<message\>`) or make the channel your primary channel to write in (example: `/m`).|
|/ccolor \<channel\> \<color\>||Changes channel name’s color. [List of color codes](http://www.minecraftwiki.net/wiki/Color_Codes). Example: `/ccolor mod b`.|
|/cmode \<channel\> \<mode\>||Changes channel’s mode. Modes: `LOCAL` (makes channel work only on the current server), `MUTE` (nobody can talk), `RAVE` (random colors for letters, useful to stop huge fights), `RANDOM` (prepends everything with `&k` to make the text obfuscated), `LOUD` (enables bold and changes text to caps), `BORING` (removes all color and formatting), `PERSIST` (makes the channel show up in `/clist` even if it’s empty)|
|/cname \<channel\> \<name\>||Changes channel’s display name. Can be used to properly capitalize letters. Example: `/cname mod MOD`|
    
# MCNSA Grab Bag

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|/dicks||A message of love.|
|/pong||Alternative to `/ping`.|
|/rand \<min\> \<max\>||Gives a random number according to `min` (default: `0`) and `max`.|
|/ranks||Lists all of the server ranks.|
