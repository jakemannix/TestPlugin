package org.getspout.testplugin;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.ListWidgetItem;

public class TestModel extends AbstractListModel {
	private ListWidgetItem[] items;
	
	public TestModel() {
		shuffle();
	}
	
	public void shuffle() {
		int count = (int) (Math.random() * 40) + 1;
		items = new ListWidgetItem[count];
		for(int i = 0; i < count; i++) {
			items[i] = new ListWidgetItem("Item hash: "+(int) (Math.random() * 100), "A test text");
		}
		sizeChanged();
	}

	@Override
	public ListWidgetItem getItem(int row) {
		if(row < 0 || row >= getSize()) {
			return null;
		}
		return items[row];
	}

	@Override
	public int getSize() {
		return items.length;
	}

	@Override
	public void onSelected(int item, boolean doubleClick) {
		System.out.println("Item "+item+" has been "+(doubleClick?"doubleclicked.":"clicked."));
	}

}
