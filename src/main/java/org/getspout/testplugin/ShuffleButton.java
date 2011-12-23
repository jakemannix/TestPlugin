package org.getspout.testplugin;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericListView;

public class ShuffleButton extends GenericButton implements Button {
	TestModel model;
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		model.shuffle();
		System.out.println("Shuffled");
	}

	public ShuffleButton(TestModel model) {
		setText("Shuffle");
		this.model = model;
	}
}
