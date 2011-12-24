package org.getspout.testplugin;

import java.util.ArrayList;

import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ComboBox;
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
		shuffle.setX(320).setY(10).setWidth(100).setHeight(20);
		attachWidget(plugin, shuffle);
		
		ComboBox combo = new TestComboBox();
		
		ArrayList<String> stuff = new ArrayList<String>();
		for(int i = 0; i < 100; i++) {
			stuff.add("Item "+i);
		}
		combo.setItems(stuff);
		combo.setText("");
		
		combo.setX(320).setY(30).setWidth(100).setHeight(20);
		attachWidget(plugin, combo);
		
		player.getMainScreen().attachPopupScreen(this);
	}
}
