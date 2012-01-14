package org.getspout.testplugin;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.ScreenshotReceivedEvent;
import org.getspout.spoutapi.gui.GenericLabel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class TestScreenListener extends ScreenListener {
    private static final Logger logger = Logger.getLogger(TestScreenListener.class.getName());
	private int tries = 0;
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getButton() instanceof GenericIdentifiableButton &&
            ((GenericIdentifiableButton)event.getButton()).getUuid() == 1234L) {
            logger.info("test button clicked " + tries + " times so far");
	        tries++;
			if (tries > 3) {
				event.getPlayer().getMainScreen().closePopup();
				event.getScreen().setDirty(true);
				event.getPlayer().getMainScreen().attachWidget(null, new GenericLabel("I'm on the main screen!"))
                    .setX(0).setY(0).setHeight(427).setWidth(240);
			}
			else {
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