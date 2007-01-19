/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author Lyman
 *
 */
public class CanvasWelcome extends Canvas {

	private Image welcome = null;

	public CanvasWelcome(int mode) {
		switch (mode) {
			case Config.SYS_CMD_OK:
				// load welcome image
		 		try	{
					welcome = Image.createImage("/image/welcome.png");
		 		}
		 		catch (IOException e) {
					System.out.println("加载 welcome.png 失败");
		 		}
				break;
			case Config.SYS_CMD_ENCODEERROR:
				welcome = null;
				break;
			case Config.SYS_CMD_UNKNOWNERROR:
				welcome = null;
				break;
		}
	}

	protected void paint(Graphics g) {
		g.setColor(0x00ffffff);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (welcome != null) {
			g.drawImage(welcome, getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.VCENTER);
		} else {
			g.drawString("WalkBook\n无法在您的手机上运行", getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.VCENTER);
		}
	}	
	
}
