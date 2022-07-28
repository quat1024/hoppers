package agency.highlysuspect.hopper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

/**
 * Calling setCreativeTab on an item seems to not... work?
 * At least it doesn't work if the creative tab doesn't belong to your item, like I want to do
 * to add my things to the vanilla redstone creative tab. I'm not sure what's going on, to be honest.
 * 
 * TODO creative search window is broken lol
 */
public class HmmItemBlock extends ItemBlock {
	public HmmItemBlock(int id, CreativeTabs fixedTab) {
		super(id);
		this.fixedTab = fixedTab;
	}
	
	private final CreativeTabs fixedTab;
	
	@Override
	public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[] { fixedTab };
	}
}
