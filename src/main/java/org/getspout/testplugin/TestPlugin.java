package org.getspout.testplugin;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.material.CustomBlock;



public class TestPlugin extends JavaPlugin {
	
	public static CustomBlock testBlock;
	public static TestPlugin instance;

	public void onDisable() {
		
		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin] Disabled!");
	}

	public void onEnable() {
		instance = this;
		//testBlock = new TestBlock(this);
		
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestSpoutListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestScreenListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, new TestBlockListener(), Priority.Normal, this);
		TestKeyBinding binding = new TestKeyBinding();
		SpoutManager.getKeyBindingManager().registerBinding("TopCatKey", Keyboard.KEY_L, "Changes global skin to topcat.",binding, this);
		SpoutManager.getKeyBindingManager().registerBinding("PartyTimeKey", Keyboard.KEY_M, "Creates a Party Test Notification",binding, this);
        SpoutManager.getKeyBindingManager().registerBinding("TestScreenie", Keyboard.KEY_B, "Tests screenshot grabbing",binding, this);
		SpoutManager.getKeyBindingManager().registerBinding("listtest", Keyboard.KEY_L, "List Test", binding, this);
		
		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin] Enabled!");
	}
	
}
