package org.getspout.testplugin;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.SpoutWorld;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.MaterialManager;

public class TestBlockListener extends BlockListener {

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);
		MaterialManager im = SpoutManager.getMaterialManager();
		
		SpoutBlock block = ((SpoutWorld) event.getBlock().getWorld()).getBlockAt(event.getBlock().getLocation());
		
		if(block.getType().equals(Material.DIRT)) {
			int id = block.getCustomBlockId();
			
//			ItemStack is = im.getCustomItemStack(TestPlugin.testBlock, 1);
//			if(id == TestPlugin.testBlock.getCustomId()) {
//				block.getWorld().dropItem(block.getLocation(), is);
//			}
		}
	}
}
