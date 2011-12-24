package org.getspout.testplugin;

import org.getspout.spoutapi.gui.ComboBox;
import org.getspout.spoutapi.gui.GenericComboBox;

public class TestComboBox extends GenericComboBox implements ComboBox {

	@Override
	public ComboBox setOpen(boolean open, boolean sendPacket) {
		super.setOpen(open, sendPacket);
		System.out.println("ComboBox "+(open?"opened":"closed"));
		return this;
	}

	@Override
	public void onSelectionChanged(int i, String text) {
		System.out.println("Selection changed to "+i+" ("+text+")");
	}

}
