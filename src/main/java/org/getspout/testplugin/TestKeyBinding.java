package org.getspout.testplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.SpoutWorld;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

import java.util.logging.Logger;

public class TestKeyBinding implements BindingExecutionDelegate {
	private static final Logger logger = Logger.getLogger(TestKeyBinding.class.getName());
	
	public void keyPressed(KeyBindingEvent event) {
		if (event.getPlayer().getActiveScreen() != ScreenType.GAME_SCREEN) return;
        String keyId = event.getBinding().getId();
        TestPlugin.BoundKeys boundKey = TestPlugin.BoundKeys.valueOf(keyId);
        if (boundKey == null) {
          return;
        }
        logger.info(boundKey.getDesc() + " pressed");
        switch(boundKey) {
          case TOP_CAT:
			SpoutManager.getAppearanceManager().setGlobalSkin(event
                .getPlayer(), "http://s3.amazonaws.com/MinecraftSkins/Top_Cat.png");
            break;
          case PARTY_TIME:
			SpoutManager.getPlayer(event.getPlayer()).sendNotification(ChatColor.RED
              + "Party time is now! " + ChatColor.YELLOW + "Oh yeah", "Testing", Material.CAKE);
            break;
          case TEST_SCREENIE:
            SpoutManager.getPlayer(event.getPlayer()).sendScreenshotRequest();
            break;
          case LIST_TEST:
        	new ListScreen(event.getPlayer(), TestPlugin.instance);
            break;
          case RECT:
            SpoutBlock currentBlock = ((SpoutWorld) SpoutManager.getPlayer(event.getPlayer()).getWorld())
                .getBlockAt(event.getPlayer().getLocation());
            buildRectangle(currentBlock);
            //
          default:
            //
        }
	}

    private void buildRectangle(SpoutBlock block) {
      Location location = block.getLocation();
    }

	public void keyReleased(KeyBindingEvent arg0) {		
	}
	
}