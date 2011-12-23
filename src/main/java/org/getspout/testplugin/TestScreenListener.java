package org.getspout.testplugin;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.ScreenshotReceivedEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestScreenListener extends ScreenListener {
	private int tries = 0;
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getButton() instanceof GenericButton && event.getButton().getText().equals("Test")) {
			if (tries > 3) {
				((SpoutPlayer) event.getPlayer()).getMainScreen().closePopup();
				event.getScreen().setDirty(true);
				((SpoutPlayer) event.getPlayer()).getMainScreen().attachWidget(null,((GenericLabel) new GenericLabel("I'm on the main screen!")).setX(0).setY(0).setHeight(427).setWidth(240));
			}
			else {
				tries++;
				event.getButton().setText("Haha, Try Again!").setDirty(true);
			}
			event.getPlayer().sendMessage("Button test successful!");
		}
	}

    @Override
    public void onScreenshotReceived(ScreenshotReceivedEvent event) {
        BufferedImage bi = event.getScreenshot();
        try {
            ImageIO.write(bi, "png", new File("spout.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}