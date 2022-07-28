# Hopper

Adds hoppers to Minecraft!

Thanks to unascribed and TwilightFlower for [doing the toolchain heavy lifting and for providing an example.](https://github.com/BuildCraft/BuildCraft/tree/3.4.x) And for being cool.

# License

Nominally `LGPL-3.0-or-later` but I literally do not give a shit if you take stuff for learning purposes and ship it in a released mod

# Etc

Changes made to the original Buildcraft-Voldeloom `build.gradle` that I think are notable:

* Zip munging in `merge_forge.sh` replaced with an implementation in the buildscript using Java NIO. Now you can run it on Windows, doesn't break if you run the script twice, etc.
* Defined a `minecraftVersion` parameter because one wasn't defined. The error wasn't noticed because it didn't crop up if you already had `mcp726a.zip` downloaded.
* I try to crash early if any file downloads as zero bytes. In particular, the Internet Archive seems to respond to 404 requests with 0-byte files. If you are still having trouble you can browse the MCP archive and download `mcp726a.zip` yourself [here](https://ia601701.us.archive.org/view_archive.php?archive=/29/items/minecraftcoderpack/minecraftcoderpack.zip).

Yes, run configs don't work, I mean voldeloom prints warnings about "fabric-installer.json not found in classpath", what did you expect would happen.