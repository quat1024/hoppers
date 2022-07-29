Just taking notes as I figure this stuff out!

# Voldeloom and You

[Voldeloom](https://github.com/unascribed/voldeloom/) is a hacked-up copy of Fabric Loom, developed by TwilightFlower and unascribed, that wrangles old MCP mapping sets and is able to build Forge mods for very old versions. It builds off the zillion years of experience the Minecraft communty has accumulated with developing file-remappers and Minecraft downloaders and stuff, to provide a working Minecraft toolchain without the brittle shell-scripts and the jank. Just kidding it's janky.

You need a version of Java new enough to support the language features that Voldeloom uses, but old enough to compile to Java 6 which is what these versions of Forge were written for. Certain important Forge tasks, like scanning the classpath for `@Mod` annotations, fail when something exists that is compiled for a too-new Java version.

Here is where I *would* put the directions to "make sure that you're using specifically only Java 11!" because I heard that's the only one that works, but I just checked and apparently I've been using Java 8 the whole time. Whoops.

IntelliJ users can go to the "Project Structure" dialog, set the JDK to an installation of Java 8 and the Project Language Level to `6`, then go to `Settings -> Build Execution Deployment -> Build Tools -> Gradle` and ensure that the correct JDK version is being used (seriously why does this setting have a mind of its own). Also when invoking `./gradlew` outside of your IDE, ensure that `JAVA_HOME` points to the correct Java version.

Run configurations were also not adjusted away from Fabric so they're just flat-out broken. Dont worry about it everything is fine!!!!! Instead, build with the usual `./gradlew build` release process then copy it into a regular Forge client installation. I suggest making a small shell script that does this then invoking it as your MultiMC/PolyMC "prelaunch command". Kinda cursed but it works.

For debugging, you might have some luck with [remote debugging](https://www.jetbrains.com/help/idea/tutorial-remote-debug.html) but the mapping set will be different in your workspace and in the forge client installation, so things will probably be pretty busted. Logspam debugging is your friend.

## Examples

Mods using Voldeloom:

* [SplashAnimation-1.4](https://git.sleeping.town/unascribed/SplashAnimation-1.4) - Small coremod
* [LegacyCustomPublish](https://github.com/unascribed/LegacyCustomPublish) and [Drogtor](https://github.com/unascribed/Drogtor/tree/1.4) - Small mods adding one command
* [Ears platform-forge-1.4](https://git.sleeping.town/unascribed/Ears/src/branch/trunk/platform-forge-1.4) - Uh oh
* [Retro Tater](https://github.com/TwilightFlower/retro-tater) - This uses a very old version of voldeloom ("cursedforge") and will require adjusting
* This mod :]

Existing codebases retrofitted with Voldeloom:

* [ChiselRetro](https://git.sleeping.town/unascribed/ChiselRetro)
* [Buildcraft 3.4.x](https://github.com/BuildCraft/BuildCraft/tree/3.4.x) (migrated from Ant)

These exist in various stages of Voldeloom's own development so expect weirdness. If you'd like to compile them yourself, you may need to make sure Voldeloom's version is `1.0.1` or newer because of [this](https://github.com/unascribed/voldeloom/pull/1).

Of course if you're just learning to mod 1.4 you will probably want to look for other mods of the time period, once you get the workspace set up it's the same as making any other Forge mod.

## This project vs. Buildcraft

Many of the tooling files in this project were taken from [Buildcraft 3.4.x](https://github.com/BuildCraft/BuildCraft/tree/3.4.x) but here are the changes I made.

* Zip munging in `merge_forge.sh` replaced with an implementation in the buildscript using Java NIO. Now you can run it on Windows, doesn't break if you run the script twice, etc.
* Defined a `minecraftVersion` parameter because one wasn't defined. The error wasn't noticed because it didn't crop up if you already had `mcp726a.zip` downloaded.
* I try to crash early if any file downloads as zero bytes. In particular, the Internet Archive seems to respond to 404 requests with 0-byte files. If you are still having trouble you can browse the MCP archive and download `mcp726a.zip` yourself [here](https://ia601701.us.archive.org/view_archive.php?archive=/29/items/minecraftcoderpack/minecraftcoderpack.zip).

## Uhh

Voldeloom buildscripts don't work on Gradle 6 and 7 (and maybe 5) because it can't figure out where to download Forge. This is workaroundable by downloading Forge manually and providing it with a local file dependency. I'm not sure why this happens but it's something to investigate.

Applying the Voldeloom plugin instantly crashes on Gradle 7 because of the `compile` -> `implementation` and `runtime` -> `runtimeOnly` rename. This needs a Voldeloom change, I have a PR for it.

# How to write Forge 1.4.7 mods ~a retrospective~

## Documentation, tutorials

Kinda scant, but who's surprised. It's Minecraft.

* [Nice beginner tutorial by KyWest on Minecraftforum.](https://www.minecraftforum.net/forums/archive/tutorials/931734-1-4-7-forge-start-modding-minecraft).
* Open source mods, especially [Buildcraft](https://github.com/BuildCraft/BuildCraft/tree/3.4.x).
* [FML 1.4](https://github.com/minecraftforge/fml/tree/1.4) source tree has comments sometimes...
* Google lol
* This document, apparently
* Uhhhh that's about it

## `@Mod`, basic forge interaction, etc

Make a class and annotate it with `@Mod`. Fill out at least `modid` and that's all you need to get Forge to print info messages about loading your mod. Forge finds these annotations by scanning the whole damn classpath and the classpath scanning code blows up if there's anything compiled for a too-new Java version on there.

Forge (as well as certain launchers) will look for an `mcmod.info` file in the root of the jar. This is a JSON file where you can fill in more metadata like a friendlier name for the mod, mod author, description, credits etc. Having an `mcmod.info` is not required. If you don't want to repeat yourself, make sure that the `modid` in the mcmod.info matches the one in your `@Mod` seriously i lost like half an hour of debugging to this, then set `useMetadata = true` in your `@Mod`, and properties like the mod's version and friendly name will be imported from there.

Lifecycle events are registered by making a one-argument method accepting, say, `FMLPreInitializationEvent`, and annotating them with `@Mod.PreInit`, `@Mod.Init` etc. For other runtime-fired events, stick the event handlers as methods in some class, annotate the methods with `@ForgeSubscribe`, then call `MinecraftForge.EVENT_BUS.register` with the class as the argument. There are three event busses; ores, terrain-gen, and everything else.

The `@SidedProxy` annotation is a service that constructs a different class depending on the physical side. Annotate a field with it, then pass the fully qualified class name to a zero-argument-constructor class in the `clientSide` and `serverSide` attributes, and Forge will construct the right one into the field. This is a good place to squirrel away side-specific code from the classloading gremlin. Keep in mind that in these older versions Mojang was more aggressive about stripping unused methods from the wrong side, so innocuous getters might be classloading bombs, watch for the `@Environment` annotations. Also if you're looking at other mod's sourcecode it's really common for modders to shove way too much shit into their "proxy" classes for some reason.

If you've ever written forge 1.12 this should all feel very familiar.

Btw, this version of Forge has some funny stuff like `ModLoader` integration (the ModLoader API was implemented in terms of the Forge API) and some stubbed bukkit stuff that didn't pan out. History!

## Logging

Use `java.util.logging.Logger`. If you have modded newer versions, this class might be in your "exclude from auto-import" list because it kept coming up when you actually wanted the logger from Log4j. You **must** call `setParent(FMLLog.getLogger());` on your logger, or messages written to it will not appear in the game log. This is why your hello world mod isn't working.

## Configuration

**Make a configuration FIRST before playing around with registering stuff.** Everything needs manually assigned numeric IDs in this version, and Configuration has utilities for helping you select them. In preinit, make a Configuration with `new Configuration(new File(preinit.getModConfigurationDirectory(), "filename.conf"));` (.conf appears to be the convention Buildcraft uses).

## Blocks

Dispense block IDs from the `Configuration` using `getBlock`, passing a String ID that vaguely describes what your block is (only used as an identifier in the config file, nowhere else) and a preferred block ID number. Most blocks should prefer a value between 256 and 4096, because the ability to use block IDs greater than 256 in the first place is a giant Forge hack and the abstraction is a bit leaky... worldgen still uses a `byte[]`. And in 1.4, block IDs less than 256 are a very precious commodity.

You may not get the ID you asked for because another mod got to it first. Always remember to call `config.save()` so that the block IDs you actually got are saved to the config file.

`Block` has a two-argument and three-argument constructor. In each case, the first parameter is the numeric block ID dispensed from the config, and the last parameter is a `Material` that works kinda like modern-day materials (just a category vaguely defining the block). The middle parameter of the three-arg constructor is a texture index, defaulting to 0 and I'll get to that later.

The `Block` constructor registers the block as a side effect. You can retrieve the `Block` instance with `Block.blocksList[blockId]`.

Apart from that things should feel fairly familiar. `BlockPos` wasn't invented yet, and `EnumFacing` exists but it's only in the wacky world of dispenser behaviors, so if you see four int parameters they are probably x/y/z/direction. Or maybe they're x/y/z/metadata, because block metadata is a thing. This is before the Redstone Update, so if a block `isBlockNormalCube` it can't provide redstone power, and you can't provide power with a strength less than 15 anyways. Fun.

## Items

Block and item IDs are kinda connected? An `ItemBlock` corresponds to a `Block` *because* they have the same numeric ID; `ItemStack#new(Block)` creates the item with the same numeric ID as the block, the default pick-block result of a block is whatever item has the same block ID, etc etc.

Forge prefers if you choose item IDs that are out-of-the-way of block IDs so that every block has a free slot for an `ItemBlock` if it wants one. The maximum item ID is 32,000. If you are creating an item that is not an `ItemBlock`, you need to request an item ID from the `Configuration`, and a warning will be issued if you pick one less than 4,096. If you are creating an `ItemBlock` you do not need to request a new item ID.

Constructing an `Item` registers it too, just like blocks. (If you look at Buildcraft source it re-registers the item by manually putting it in the items list array, this is a probably a mistake or legacy code or something.)

An important wrinkle is that the `Item` and `ItemBlock` constructors expect you to pass the item ID **minus 256**. Vanilla Minecraft only has 256 blocks and it registers an `ItemBlock` for each of them, so I think the intention was to allow a little syntax sugar in vanilla so that `new Item(0)`, `new Item(1)`, `new Item(2)` etc corresponded to item IDs separate from the blocks. If you are creating your own `ItemBlock`, you must subtract 256 from the block ID you are creating. Confusingly, `Configuration#getItem` expects its parameter to be passed directly to the `Item` constructor and already subtracts 256 for you, so the valid range of item IDs for that method is actually 3,840 to 31,744.

(todo: setCreativeTab does not appear to work, at least not for vanilla creative tabs? Overriding `Item#getCreativeTabs` seems to be enough. Investigate this.)

### More about blocks and items

Forge keeps track of each block and item's *class* and stores it in level.dat along with the numeric ID. If you change the name of the class that implements a given block or item ID, Forge will display an interstitial warning screen when opening a world save. Keep this in mind when making updates.

Block IDs are more limited than item IDs.

Make sure to call `Block#setBlockName` and `Item#setItemName`. They are not "registry names", those aren't invented yet, but some mods still expect this to be a globally unique String (i.e. put your modid into it) that's a little nicer than a numeric block ID. This is also the key used for localizations, `myBlock.setBlockName("mymod-hi")` will make it respond to the language key `tile.mymod-hi.name`.

## Localization

At this time, language files and localizations are a Forge concept, interacted with through the `LanguageRegistry` class. 

The easy way is to hardcode en_US lang entries with `LanguageRegistry.addName`. Pass in a Block or Item as the first parameter and the English name of the thing as the second parameter. Done. To support multiple languages, call `LanguageRegistry.instance().addNameForObject()` with the language identifier in the middle parameter.

The moderately more difficult way is to use `LanguageRegistry.instance().loadLocalization`. This takes the name of a file to load with `Class.getResource` + the language identifier that the file is for, and feeds it to a java `Properties` instance. The third parameter controls whether it goes to the familiar key-value `.properties` parser (false) or the XML based one (true). Don't forget to include a leading slash on the argument fed to `Class.getResource`.

Make sure to capitalize the second half of language identifiers (en_US, not en_us). The lowercasening didn't happen yet.

## Textures

Oh boy, textures.

Search up `terrain.png` with your editor. You will find a 256x256 image that includes every block texture in the game (or more accurately, every texture that will be used when meshing terrain models, because it also includes the block-breaking effect). You can see how there's only a couple of unused spaces left, which is why they moved away from this model in 1.5. A similar file exists for "items that don't look like blocks" under `gui/items.png`.

It is divided into a 16x16 grid where each cell is 16 by 16 pixels. The upper left grid cell is texture index 0, the one to the right is texture index 1, and so on. Texture indexes 0-15 are on the top row, indexes 16-31 are on the row under it, etc. There are 256 texture indexes in total per atlas. They are always 16x16 and can't be made more high-resolution. It is conventional to spell out texture indexes like `y * 16 + x` so they have a sort of X and Y coordinate system.

To choose a non-vanilla texture atlas for your blocks and items, call `MinecraftForgeClient.preloadTexture` some time in preinit or init with the full path to the texture (you will need a client proxy for this), then in the Block constructor or wherever, call `setTextureFile` with the same path.

`ResourceLocation` wasn't invented yet, so it's customary to include your modid somewhere in the file path. The `assets` folder also wasn't invented so it has no special meaning. A file path of `gfx/mymod/terrain.png` will refer to the file `gfx/mymod/terrain.png` within the jar, which will probably be at `src/main/resources/gfx/mymod/terrain.png` in the source tree if you have the typical file layout. 

To choose a different texture index for your blocks - if you have a simple block that is the same texture on all sides, pass it as the second parameter in the three-argument Block constructor. You can also override `getBlockTextureFromSide` or `getBlockTextureFromSideAndMetadata` for cuboid blocks with different textures on each face (see `ForgeDirection` for the meaning of the `side` parameter, tldr 0-5 -> down up north south west east)

To choose a different texture index for your items - call `setIconCoord` with the X and Y position of the texture index (it computes `y * 16 + x` for you), or set the index directly with `setIconIndex`. Doing this on a `ItemBlock` will turn off the 3d-model-in-inventory rendering, but note that the texture atlas used will be queried from the Block, not the item (that's Forge's default behavior it adds in `ItemBlock`, you can override it if you really need it)

## "Models"

The easiest models to make are solid cuboids. Override `getBlockTextureFromSide`/`getBlockTextureFromSideAndMetadata` to customize all six faces of the cube, and call `setBlockBounds` to make models smaller than a full block (the UVs are cropped accordingly, so if your block exists in the center of the blockspace the texture should be in the center of the 16x16 cell).

The next hardest models to make are retextures of existing vanilla models. Override `Block#getRenderType`. This is used in `RenderBlocks#renderBlockByRenderType` which switches off the returned int and... dispatches to a giant list of block models. 0 is the standard "block consisting of one cuboid", 1 is a flower or sapling cross model, 2 is a torch, etc etc. There are 35 different types (numbered 0 through 35, not a typo, they skipped 22.) These generally have some unspoken assumptions about what kinds of textures you return from which sides of the block, or sometimes don't support different textures per-face or `setBlockBounds`, or have weird hardcoded behavior if you're using texture index 3 because that's the grass-block-side texture in vanilla. Leaky abstractions abound. If you want to use a vanilla render type read the implementation code carefully.

At a fairly significant step up in difficulty, you can make fully custom meshed blocks. Forge adds a `default` case to the big switch that passes through to `RenderingRegistry.renderWorldBlock`. Obtain a unique render type int ID with `RenderingRegistry.getNextAvailableRenderID`, then add an `ISimpleBlockRenderingHandler` with `RenderingRegistry.registerBlockHandler`. Return the render type ID in your block's `getRenderType` and now your rendering handler will be called when it's time to mesh the block. You will probably want to look at vanilla `RenderBlocks` for examples of how to work with the model mesher. You will interact with `Tesselator.instance`, most likely, or delegate to the vanilla `RenderBlock` which has lots of utility methods. Try not to mess up the GL state too much.

(Consider it a creative push to stay closer to Minecraft's blocky art style.)

If you go wild with custom models, don't forget about the block break and running-on-block particles. These are still sourced from the Block's regular texture file and texture index, make sure to put something sensible there.

I've been told that this system was kept pretty much unchanged through Minecraft 1.7.10. If you need more help or examples there's plenty of 1.7 sources to go around.

## Tile entities

Not "block entities"! This is MCP.

Vanilla requires blocks to implement `BlockContainer` and implement the abstract method `createNewTileEntity` to make the tile entity. Forge extends this; you can override `hasTileEntity` and make a new tile entity in `createTileEntity`, both of which are now metadata-sensitive. If you don't need to extend another base class, it doesn't hurt to extend BlockContainer, which also takes care of common utilities such as adding and removing the TileEntity when you place and break the block.

To register the tileentity class itself, call `GameRegistry.registerTileEntity`. First param is the tile entity class, second param is a string which is the globally unique string ID of the tile entity. A zero-argument constructor on the TileEntity class is required for the reflective call in `TileEntity.createAndLoadEntity`.

For tile entity special renderers, in your proxy call `ClientRegistry.bindTileEntitySpecialRenderer`. First param is the tile entity class again, second param is an instance of `TileEntitySpecialRenderer`.

All tile entities are tickable, override `updateEntity`. `markDirty`/`setChanged` is spelled as `TileEntity#onInventoryChanged` which is a terrible name you can blame SpecialSource for.

# Things that are not bugs in your mod

* The text in container guis gets slightly lighter in color when you are holding an item in your hand. It just does that.
* Your hand still bobs up and down when trying to place a block in a position where it doesn't fit, it doesn't mean the item was implemented wrong.
* A hand swing animation is always displayed when right clicking on an entity.

These are all Forge bugs lol. Actually the last one is a feature that lets you pet animals and/or your friends.