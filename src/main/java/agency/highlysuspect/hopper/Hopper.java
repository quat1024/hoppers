package agency.highlysuspect.hopper;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.logging.Logger;

@Mod(
	name = Hopper.NAME,
	version = Hopper.VERSION,
	modid = Hopper.MODID
)
public class Hopper {
	public static final String NAME = "Hopper";
	public static final String MODID = "hopper";
	public static final String VERSION = "${version}";
	
	public static final Logger LOGGER = Logger.getLogger(NAME);
	
	@Mod.PreInit
	public void preinit(FMLPreInitializationEvent e) {
		LOGGER.setParent(FMLLog.getLogger());
		
		LOGGER.info("Hello Ancient World!");
		LOGGER.warning("Hello Ancient World!");
	}
}
