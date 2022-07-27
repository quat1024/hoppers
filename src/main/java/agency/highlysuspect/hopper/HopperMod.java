package agency.highlysuspect.hopper;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.logging.Logger;

@Mod(
	name = HopperMod.NAME,
	version = HopperMod.VERSION,
	modid = HopperMod.MODID
)
public class HopperMod {
	public static final String NAME = "Hopper";
	public static final String MODID = "hopper";
	public static final String VERSION = "${version}";
	
	public static final Logger LOGGER = Logger.getLogger(NAME);
	
	public static Configuration config;
	
	public static BlockRedstone blockRedstone;
	
	@Mod.PreInit
	public void preinit(FMLPreInitializationEvent pre) {
		LOGGER.setParent(FMLLog.getLogger());
		LOGGER.info("Hello world!");
		
		config = new Configuration(new File(pre.getModConfigurationDirectory(), "hopper.conf"));
		try {
			config.load();
			
			blockRedstone = new BlockRedstone(config.getBlock("hopper-redstoneBlock.id", 730).getInt(), Material.rock);
			blockRedstone.setBlockName("hopper-redstoneBlock");
			
			ItemBlock itemBlockRedstone = new ItemBlock(blockRedstone.blockID - 256);
			itemBlockRedstone.setCreativeTab(CreativeTabs.tabRedstone);
			Item.itemsList[blockRedstone.blockID] = itemBlockRedstone;
			
		} finally {
			config.save();
		}
		
		//TODO: Mess with LanguageRegistry.loadLangaugeTable
		LanguageRegistry.addName(blockRedstone, "Block of Redstone");
		
		//TODO: Use a proxy like a real modder lol
		MinecraftForgeClient.preloadTexture("gfx/hopper/blocks.png");
	}
}
