package org.getspout.testplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.gui.ScreenType;

public class TestKeyBinding implements BindingExecutionDelegate {
	
	
	public void keyPressed(KeyBindingEvent event) {
		if (event.getPlayer().getActiveScreen() != ScreenType.GAME_SCREEN) return;
		if (event.getBinding().getId() == "TopCatKey") {
			SpoutManager.getAppearanceManager().setGlobalSkin(event.getPlayer(), "http://s3.amazonaws.com/MinecraftSkins/Top_Cat.png");
		} else if (event.getBinding().getId() == "PartyTimeKey") {
			SpoutManager.getPlayer(event.getPlayer()).sendNotification(ChatColor.RED + "Party time is now! " + ChatColor.YELLOW + "Oh yeah", "Testing", Material.CAKE);
		} else if (event.getBinding().getId().equalsIgnoreCase("TestScreenie")) {
            SpoutManager.getPlayer(event.getPlayer()).sendScreenshotRequest();
        } else if (event.getBinding().getId().equalsIgnoreCase("listtest")) {
        	new ListScreen(event.getPlayer(), TestPlugin.instance);
        }
	}

	public void keyReleased(KeyBindingEvent arg0) {		
	}
	
}