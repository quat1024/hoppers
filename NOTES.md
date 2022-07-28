# How to write Forge 1.4.7 mods, a retrospective

## Documentation

Scant.

* [Buildcraft's 3.4.x source.](https://github.com/BuildCraft/BuildCraft/tree/3.4.x)
* [FML 1.4](https://github.com/minecraftforge/fml/tree/1.4) has comments sometimes.
* [Nice beginner tutorial by KyWest.](https://www.minecraftforum.net/forums/archive/tutorials/931734-1-4-7-forge-start-modding-minecraft) enable javascript to see the spoilerboxes.

## Mod Main Class

Make a class and annotate it with `@Mod`. Fill out at least `version` and `modid`. There is nothing else you need to do. I'm not sure exactly how Forge discovers that this class exists - there's no metadata written to the jar or anything - but it does.

## Logging

Use `java.util.logging.Logger`. If you have modded newer versions, this class might be in your "exclude from auto-import" list because it kept coming up when you actually wanted the logger from Log4j. You **must** call `setParent(FMLLog.getLogger());` on your logger, or messages written to it will not appear in the game log. This is a gotcha when making your first hello world mod.

## Events

Lifecycle events are registered by annotating them with `@Mod.PreInit`, `@Mod.Init` etc. For other events, stick the event handlers as methods in a class, annotate the methods with `@ForgeSubscribe`, then call `MinecraftForge.EVENT_BUS.register(ThatClass.class)`.

## Sided Proxy

The `@Mod.SidedProxy` annotation is a service that constructs a different class depending on the physical side, pass the fully qualified class name on the `clientSide` and `serverSide` attributes. This is a good place to squirrel away side-specific code from the classloading gremlin. If you've written forge 1.12 this will be familiar.

## Configuration

Make a configuration FIRST, before registering blocks. Blocks need manually assigned numeric IDs in this version and Forge has utilities for helping you assign unique IDs in Configuration.

In preinit, make a Configuration with `new Configuration(new File(preinit.getModConfigurationDirectory(), "filename.conf"));` (.conf appears to be the thing Buildcraft uses) and stash it in a public static field on your mod class.

Then, dispense block IDs with `config.getBlock`, passing a String ID that vaguely defines what your block is (it's only used as an identifier in the config file, nowhere else), and a default/preferred ID that is greater than 256. A warning is printed about "modders should not register blocks with IDs less than 256 unless they are needed for terrain generation" -- Terrifying! I don't want to know what that is about!

When you're done dispensing IDs don't forget to call `config.save`. If you didn't get an ID that you asked for you should save this to the config so that you are sure to get the same ID next time the game is loaded (even if mods are added or removed.)

## Blocks

Make a class that extends Block. There is a two-argument and three-argument constructor. In each case, the first parameter is the numeric block ID, which you should dispense from your configuration, and the last parameter is a `Material` that works kinda like modern-day materials (just a category vaguely defining the block).

The middle parameter of the three-arg constructor is a texture index, defaulting to 0. I'll get to that later.

Apart from that things should be fairly familiar. Some funnies:

* If a function takes a `World` or `IBlockAccess` with four int parameters, they are probably x/y/z/direction the call is coming from, or x/y/z/metadata.
* If a block `isBlockNormalCube`, it cannot provide redstone power!
* You do not need to register the block as a separate step, the constructor does that as a side effect. And by "register" I mean "put it in the `public static final Block[] blocksList`" which is the only way to register a block, and also how you obtain an instance of `Block` given its numeric ID.

## Items

The `BlockItem` constructor takes an ID number, which is the ID of the block it places... minus 256. Yeah I dunno. Literally every method in the `BlockItem` constructor adds 256 back to the passed argument before using it, except for `super`, but the `Item` constructor also adds 256 to the passed-in ID.

The `Item` constructor also adds to the `public static final Item[] itemsList` array so there is no separate step needed. (If you look at Buildcraft source it re-registers the item by manually putting it in the items list array, this is a probably a mistake or legacy code or something.)

`BlockItem`s must have the same numeric ID as the `Block` they place. This is a well-used assumption - for example, `ItemStack#new(Block)` creates the itemstack by taking the numeric ID of the block, so it must correspond to an item.

setCreativeTab does not appear to work, at least not for vanilla creative tabs? Overriding `Item#getCreativeTabs` seems to be enough. Investigate this.

Todo: items that aren't `BlockItem`s, are they different?

### Important note about classes

Forge keeps track of each block and item's *class* and stores it in level.dat, along with the numeric ID. If you change the name of the class that implements a given block or item ID, Forge will display an interstitial warning screen when opening a world save. Keep this in mind when making updates.

## Localization

Lang files are not automatically loaded. `LanguageRegistry`, a forge class, is your friend here. 

The easy way is to hardcode en_US lang entries with `LanguageRegistry.addName`. Pass in a Block or Item as the first parameter and the English name of the thing as the second parameter. Done. To support multiple languages, call `LanguageRegistry.instance().addNameForObject()` with the language identifier in the middle parameter.

The moderately more difficult way is to use `LanguageRegistry.instance().loadLocalization`. This takes the name of a file to load with `Class.getResource` + the language identifier that the file is for, and feeds it to a java `Properties` instance. The third parameter controls whether it goes to the familiar key-value `.properties` parser (false) or the XML based one (true). Make sure to call `Block#setName` to configure a language key. Don't forget to include a leading slash on the argument fed to `Class.getResource`.

Make sure to capitalize the second half of language identifiers (en_US, not en_us). The lowercasening didn't happen yet.

## Textures

Oh boy, textures.

Search up `terrain.png` with your editor. You will find a 256x256 image that includes every block texture in the game (or more accurately, every texture that will be used when meshing terrain models, because it also includes the block-breaking effect). You can see how there's only a couple of unused spaces left, which is why they moved away from this model in 1.5. A similar file exists for "items that don't look like blocks" under `gui/items.png`.

It is divided into a 16x16 grid where each cell is 16 by 16 pixels. The upper left grid cell is texture index 0, the one to the right is texture index 1, and so on. Texture indexes 0-15 are on the top row, indexes 16-31 are on the row under it, etc. There are 256 texture indexes in total per atlas. They are always 16x16 and can't be made more high-resolution. It is conventional to spell out texture indexes like `y * 16 + x` so they have a sort of X and Y coordinate system.

To choose a non-vanilla texture atlas for your blocks and items, call `MinecraftForgeClient.preloadTexture` some time in preinit or init with the full path to the texture (you will need a client proxy for this), then in the Block constructor or wherever, call `setTextureFile` with the same path.

`ResourceLocation` wasn't invented yet, so it's customary to include your modid somewhere in the file path. The `assets` folder also wasn't invented so it has no special meaning, but you can use it if you want. A file path of `gfx/mymod/terrain.png` will refer to the file `gfx/mymod/terrain.png` within the jar, which will probably be at `src/main/resources/gfx/mymod/terrain.png` in the source tree, unless you customized the gradle resources dir like Buildcraft did. 

To choose a different texture index for your blocks - if you have a simple block that is the same texture on all sides, pass it as the second parameter in the three-argument Block constructor. You can also override `getBlockTextureFromSide` or `getBlockTextureFromSideAndMetadata` for cuboid blocks with different textures on each face (see `ForgeDirection` for the meaning of the `side` parameter, tldr 0-5 -> down up north south west east)

To choose a different texture index for your items, call `setIconCoord` with the X and Y position of the texture index (it computes `y * 16 + x` for you), or directly with `setIconIndex`. Doing this on a `BlockItem` will turn off the 3d-model-in-inventory rendering, but note that the texture atlas used will be queried from the Block, not the item (you can override it, but that's Forge's default behavior)

## Models

The easiest models to make are solid cuboids. Override `getBlockTextureFromSide`/`getBlockTextureFromSideAndMetadata` to customize all six faces of the cube, and call `setBlockBounds` to make models smaller than a full block (the UVs are cropped accordingly, so if your block exists in the center of the blockspace, the texture should be in the center of the 16x16 cell).

The next hardest models to make are retextures of existing vanilla models. Override `Block#getRenderType`. This is used in `RenderBlocks#renderBlockByRenderType` which switches off the returned int and... dispatches to a giant list of block models. 0 is the standard "block consisting of one cuboid", 1 is a flower or sapling cross model, 2 is a torch, etc etc. These generally have some unspoken assumptions about what kinds of textures you return from which sides of the block, or sometimes don't even support different textures per-face; read the implementation code carefully.

At a fairly significant step up in difficulty, you can make fully custom meshed blocks. Forge adds a `default` case to the big switch that passes through to `RenderingRegistry.renderWorldBlock`. Obtain a unique render type int ID with `RenderingRegistry.getNextAvailableRenderID`, then add an `ISimpleBlockRenderingHandler` with `RenderingRegistry.registerBlockHandler`. Return the render type ID in your block's `getRenderType` and now your rendering handler will be called when it's time to mesh the block. You will probably want to look at vanilla `RenderBlocks` for examples of how to work with the model mesher. You will interact with `Tesselator.instance`, most likely.

(Consider it a creative push to stay closer to Minecraft's blocky art style.)

If you go wild with custom models, don't forget about the block break and running-on-block particles. These are still sourced from the Block's regular texture file and texture index, so make sure to put something sensible there.

## Tile entities

Not "block entities"! This is MCP.

Vanilla requires blocks to implement `BlockContainer` and implement the abstract method `createNewTileEntity` to make the tile entity. Forge seems to extend this; you can override `hasTileEntity` and create/return the tile entity in `createTileEntity`, both of which are now metadata-sensitive. If you don't need to extend another base class, it doesn't hurt to extend BlockContainer, which also takes care of common utilities such as adding and removing the TileEntity when you place and break the block.

To register the tileentity class itself, call `GameRegistry.registerTileEntity`. First param is the tile entity class, second param is a string which is the globally unique string ID of the tile entity. A zero-argument constructor on the TileEntity class is required for the reflective call in `TileEntity.createAndLoadEntity`.

For tile entity special renderers, in the proxy call `ClientRegistry.bindTileEntitySpecialRenderer`. First param is the tile entity class again, second param is an instance of `TileEntitySpecialRenderer`.

All tile entities are tickable, override `updateEntity`. In an example of a truly terrible MCP name, `markDirty`/`setChanged` is spelled as `TileEntity#onInventoryChanged`. And that's pretty much all there is to tile entities, much simpler than it is in the recent versions.

# Things that are not bugs in your mod

The text in container guis gets slightly lighter in color when you are holding an item in your hand. It just does that.

Your hand still bobs up and down when trying to place a block in a position where it doesn't fit, it doesn't mean the item was implemented wrong.