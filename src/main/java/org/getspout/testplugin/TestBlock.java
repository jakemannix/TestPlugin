package org.getspout.testplugin;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class TestBlock extends GenericCubeCustomBlock {
	
	public static String textureUrl = "http://dl.dropbox.com/u/40267690/quartz.png";
	public static Texture texture;

	public TestBlock(Plugin plugin) {
		super(plugin, "CustomBlock", true, new GenericCubeBlockDesign(plugin,
            new Texture(plugin, textureUrl, 16, 16, 16), 0));
	}

}
