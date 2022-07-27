package agency.highlysuspect.hopper;

import agency.highlysuspect.hopper.proxy.CommonProxy;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
	
	@SidedProxy(clientSide = "agency.highlysuspect.hopper.proxy.ClientProxy", serverSide = "agency.highlysuspect.hopper.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static Configuration config;
	
	public static BlockRedstone blockRedstone;
	public static ItemBlock itemBlockRedstone;
	public static BlockHopper blockHopper;
	public static ItemBlock itemBlockHopper;
	
	@Mod.PreInit
	public void preinit(FMLPreInitializationEvent pre) {
		LOGGER.setParent(FMLLog.getLogger());
		LOGGER.info("Hello world!");
		
		config = new Configuration(new File(pre.getModConfigurationDirectory(), "hopper.conf"));
		try {
			config.load();
			
			blockRedstone = new BlockRedstone(config.getBlock("hopper-redstoneBlock.id", 730).getInt());
			blockRedstone.setBlockName("hopper-redstoneBlock");
			itemBlockRedstone = new HmmItemBlock(blockRedstone.blockID - 256, CreativeTabs.tabRedstone);
			
			blockHopper = new BlockHopper(config.getBlock("hopper-hopperBlock.id", 731).getInt());
			blockHopper.setBlockName("hopper-hopperBlock");
			itemBlockHopper = new HmmItemBlock(blockHopper.blockID - 256, CreativeTabs.tabRedstone);
		} finally {
			config.save();
		}
		
		//TODO: Mess with LanguageRegistry.loadLangaugeTable and do this correctly lol
		LanguageRegistry.addName(blockRedstone, "Block of Redstone");
		LanguageRegistry.addName(blockHopper, "Hopper");
	}
	
	@Mod.Init
	public void init(FMLInitializationEvent e) {
		proxy.clientInit();
		
		//9 redstone -> 1 redstone block
		GameRegistry.addShapelessRecipe(new ItemStack(itemBlockRedstone), Item.redstone, Item.redstone, Item.redstone, Item.redstone, Item.redstone, Item.redstone, Item.redstone, Item.redstone, Item.redstone);
		
		//1 redstone block -> 9 redstone
		GameRegistry.addShapelessRecipe(new ItemStack(Item.redstone, 9), itemBlockRedstone);
		
		//Hopper
		GameRegistry.addShapedRecipe(new ItemStack(itemBlockHopper),
			"i i",
			"ici",
			" i ",
			'i', Item.ingotIron,
			'c', Block.chest);
	}
}
