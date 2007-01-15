/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Font;


/**
 * @author Lyman
 *
 */

public class FormOption extends Form {
	
	private ChoiceGroup DisplayColor = new ChoiceGroup("颜色", Choice.EXCLUSIVE);
	private ChoiceGroup FontFace = new ChoiceGroup("字体", Choice.EXCLUSIVE);
	private ChoiceGroup FontStyle = new ChoiceGroup("字体样式", Choice.MULTIPLE);
	private ChoiceGroup FontSize = new ChoiceGroup("字体大小", Choice.EXCLUSIVE);
	private ChoiceGroup LineHeight = new ChoiceGroup("行距", Choice.EXCLUSIVE);
	private Gauge ScrollSpeed = new Gauge("自动阅读速度", true, 5, Config.INSTANCE.getScrollSpeed());
	private Gauge RMSSpace = null;

	public FormOption() {
		super("WalkBook阅读设置");
		int s = RMSAgent.INSTANCE.getSpaceOccupied() + RMSAgent.INSTANCE.getSpaceAvailable();
		RMSSpace = new Gauge("存储空间(" + (s / 1024) + "k)", false, s, RMSAgent.INSTANCE.getSpaceOccupied());
		this.append(RMSSpace);
		DisplayColor.append("蓝底黄字", null);
		DisplayColor.append("黑底绿字", null);
		DisplayColor.append("白底黑字", null);
		this.append(DisplayColor);
		this.append(ScrollSpeed);
		FontFace.append("系统字体", null);
		FontFace.append("等宽字体", null);
		FontFace.append("比例字体", null);
		this.append(FontFace);
		FontStyle.append("普通", null);
		FontStyle.append("加粗", null);
		FontStyle.append("倾斜", null);
		FontStyle.append("下划线", null);
		this.append(FontStyle);
		FontSize.append("小", null);
		FontSize.append("中", null);
		FontSize.append("大", null);
		this.append(FontSize);
		LineHeight.append("无", null);
		LineHeight.append("1像素", null);
		LineHeight.append("2像素", null);
		LineHeight.append("0.5倍字高", null);
		LineHeight.append("1倍字高", null);
		this.append(LineHeight);
		// set default value
		switch (Config.INSTANCE.getFontColor()) {
			case 0x0000ffff:
				DisplayColor.setSelectedIndex(0, true);
				break;
			case 0x0000ff00:
				DisplayColor.setSelectedIndex(1, true);
				break;
			case 0x00000000:
				DisplayColor.setSelectedIndex(2, true);
				break;
		}
		ScrollSpeed.setValue(Config.INSTANCE.getScrollSpeed());
		switch (Config.INSTANCE.getFont().getFace()) {
			case Font.FACE_SYSTEM:
				FontFace.setSelectedIndex(0, true);
				break;
			case Font.FACE_MONOSPACE:
				FontFace.setSelectedIndex(1, true);
				break;
			case Font.FACE_PROPORTIONAL:
				FontFace.setSelectedIndex(2, true);
				break;
		}
		if (Config.INSTANCE.getFont().getStyle() == Font.STYLE_PLAIN) {
			FontStyle.setSelectedIndex(0, true);
		} else {
			if ((Config.INSTANCE.getFont().getStyle() & Font.STYLE_BOLD) > 0) {
				FontStyle.setSelectedIndex(1, true);
			}
			if ((Config.INSTANCE.getFont().getStyle() & Font.STYLE_ITALIC) > 0) {
				FontStyle.setSelectedIndex(2, true);
			}
			if ((Config.INSTANCE.getFont().getStyle() & Font.STYLE_UNDERLINED) > 0) {
				FontStyle.setSelectedIndex(3, true);
			}
		}
		switch (Config.INSTANCE.getFont().getSize()) {
			case Font.SIZE_SMALL:
				FontSize.setSelectedIndex(0, true);
				break;
			case Font.SIZE_MEDIUM:
				FontSize.setSelectedIndex(1, true);
				break;
			case Font.SIZE_LARGE:
				FontSize.setSelectedIndex(2, true);
				break;
		}
		switch (Config.INSTANCE.getLineHeight()) {
		case 0:
			LineHeight.setSelectedIndex(0, true);
			break;
		case 1:
			LineHeight.setSelectedIndex(1, true);
			break;
		case 2:
			LineHeight.setSelectedIndex(2, true);
			break;
		case 50:
			LineHeight.setSelectedIndex(3, true);
			break;
		case 100:
			LineHeight.setSelectedIndex(4, true);
			break;
		}
	};

	public int getFontColor() {
		int i = Config.DEFAULT_FONTCOLOR;
		switch (DisplayColor.getSelectedIndex()) { 
			case 0:
				i = Config.COLOR_YELLOW;
				break;
			case 1:
				i = Config.COLOR_GREEN;
				break;
			case 2:
				i = Config.COLOR_BLACK;
				break;
		}
		return i;
	}

	public int getBGColor() {
		int i = Config.DEFAULT_BGCOLOR;
		switch (DisplayColor.getSelectedIndex()) { 
			case 0:
				i = Config.COLOR_BLUE;
				break;
			case 1:
				i = Config.COLOR_BLACK;
				break;
			case 2:
				i = Config.COLOR_WHITE;
				break;
		}
		return i;
	}

	public Font getFont() {
		int face = 0;
		int style = 0;
		int size = 0;
		switch (FontFace.getSelectedIndex()) {
			case 0:
				face = Font.FACE_SYSTEM;
				break;
			case 1:
				face = Font.FACE_MONOSPACE;
				break;
			case 2:
				face = Font.FACE_PROPORTIONAL;
				break;
		}
		if (FontStyle.isSelected(0)) {
			style = Font.STYLE_PLAIN;
		}
		if (FontStyle.isSelected(1)) {
			style = style | Font.STYLE_BOLD;
		}
		if (FontStyle.isSelected(2)) {
			style = style | Font.STYLE_ITALIC;
		}
		if (FontStyle.isSelected(3)) {
			style = style | Font.STYLE_UNDERLINED;
		}
		switch (FontSize.getSelectedIndex()) {
			case 0:
				size = Font.SIZE_SMALL;
				break;
			case 1:
				size = Font.SIZE_MEDIUM;
				break;
			case 2:
				size = Font.SIZE_LARGE;
				break;
		}
		return Font.getFont(face, style, size);
	}

	public int getScrollSpeed() {
		return ScrollSpeed.getValue();
	}
	
	public int getLineHeight() {
		int i = Config.DEFAULT_LINEHEIGHT;
		switch (LineHeight.getSelectedIndex()) { 
			case 0:
				i = 0;
				break;
			case 1:
				i = 1;
				break;
			case 2:
				i = 2;
				break;
			case 3:
				i = 50;
				break;
			case 4:
				i = 100;
				break;
		}
		return i;
	}

}