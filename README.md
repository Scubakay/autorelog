# AutoRelog

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/PZILPxJ6?label=modrinth&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+)](https://modrinth.com/mod/autorelog)
![Modrinth Followers](https://img.shields.io/modrinth/followers/PZILPxJ6?color=#97ca00)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/PZILPxJ6?color=#97ca00)

A mod that lets you automatically reconnect to a restarting Minecraft server when AFK.

Make sure to have proper overflow protection in your farms!

## Automatic mode

AutoRelog wil automatically try to reconnect 60 seconds after you are disconnected. If you
want to activate AutoRelog manually, check out the Mod Menu settings, or change the setting
in the `autorelog.json` file to `"mode": "AUTOMATIC"`.

## Manual mode

- Type `/autorelog` to activate automatic reconnection after 60 seconds.
- Disconnect by leaving the server manually or use `/autorelog` again to deactivate.


# FAQ

## Will AutoRelog be backported to Minecraft 1.x.x?

Since we use Stonecutter now (#37) it's much easier to backport to older versions. But since I don't necessarily play 
those old versions, I'll wait with backporting further than 1.19 for now.

If you play on an older version, and you want to use AutoRelog, just create 
[a ticket](https://github.com/Scubakay/autorelog/issues/new) titled "1.18 support" or something, and I'll see if I have 
time for it.

## Version 1.x.x of AutoRelog crashes/doesn't work

All the versions should work, but sometimes I miss something in the changes in Minecraft, Fabric, or the Yarn mappings.
If you encounter any crashes or other issues just create [a ticket](https://github.com/Scubakay/autorelog/issues/new), 
and I'll test for that version specifically.

## Will AutoRelog be ported to Forge/Neoforge?

I mostly play with Fabric mods, so I don't really have a need for a Forge version of AutoRelog. However, adding support
might be something I'd like to add in the future.

# Contributing

If you want to help out by creating a pull request, please add version updates to a separate pull request.

## Thanks for your contribution!

- [yichifauzi](https://github.com/yichifauzi) and [notlin4](https://github.com/notlin4) for Chinese translations
- [EEHoveckis](https://github.com/EEHoveckis) for Latvian translations