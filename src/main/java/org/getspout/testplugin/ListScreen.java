package org.getspout.testplugin;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericListView;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ListScreen extends GenericPopup {
	TestModel model = new TestModel();
	private GenericListView view = new GenericListView(model);
	private TestPlugin plugin;
	
	public ListScreen(SpoutPlayer player, TestPlugin plugin) {
		this.plugin = plugin;
		view.setX(10).setY(10).setWidth(300).setHeight(200);
		view.setAutoDirty(true);
		attachWidget(plugin, view);
		
		Button shuffle = new ShuffleButton(model);
		shuffle.setX(320).setY(10).setWidth(150).setHeight(20);
		attachWidget(plugin, shuffle);
		
		player.getMainScreen().attachPopupScreen(this);
	}
}
