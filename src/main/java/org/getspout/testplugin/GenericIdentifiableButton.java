package org.getspout.testplugin;

import org.getspout.spoutapi.gui.GenericButton;

public class GenericIdentifiableButton extends GenericButton {

  private final long uuid;

  public GenericIdentifiableButton(long uuid) {
    this.uuid = uuid;
  }

  public GenericIdentifiableButton(long uuid, String text) {
    super(text);
    this.uuid = uuid;
  }

  public long getUuid() {
    return uuid;
  }
}
