package org.getspout.testplugin;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.material.CustomBlock;



public class TestPlugin extends JavaPlugin {
	
	public static CustomBlock testBlock;

	public void onDisable() {
		
		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin] Disabled!");
	}

	public void onEnable() {
		
		testBlock = new TestBlock(this);
		
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestSpoutListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestInputListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestScreenListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, new TestBlockListener(), Priority.Normal, this);
		
		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin] Enabled!");
	}
	
}