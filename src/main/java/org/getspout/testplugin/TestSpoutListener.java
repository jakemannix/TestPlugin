package org.getspout.testplugin;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TestSpoutListener extends SpoutListener {

	@Override
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
		event.getPlayer().sendMessage("Testing commences!");
		
		TestPlugin pl = TestPlugin.instance;
		
		GenericPopup popup = new GenericPopup();
		
		GenericButton button = new GenericButton("Test");
		button.setX(0).setY(0).setHeight(20).setWidth(200);
		popup.attachWidget(pl, button);
		
		((SpoutPlayer) event.getPlayer()).getMainScreen().attachPopupScreen(popup);
	}
}
