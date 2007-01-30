/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.Font;

/**
 * @author Lyman
 *
 */
public class Config {
	// system
	public static boolean DEBUG = false;
	
	public static final int MAX_BOOK = 9;
	public static final String CODE = "UTF-8";
	public static final int WAITING_INTERVAL = 200;		// in ms
	// RMS related
	public static final String RMSName = "WALKBOOK";
	public static final int RMS_POS_CONF = 1;
	public static final int RMS_POS_BOOK = 2;
	// color
	public static final int COLOR_BLUE   = 0x000000ff;
	public static final int COLOR_YELLOW = 0x00ffff00;
	public static final int COLOR_GREEN  = 0x0000ff00;
	public static final int COLOR_BLACK  = 0x00000000;
	public static final int COLOR_WHITE  = 0x00ffffff;
	// default
	public static final int DEFAULT_FONTCOLOR = COLOR_GREEN;
	public static final int DEFAULT_BGCOLOR = COLOR_BLACK;
	public static final int DEFAULT_SCROLLSPEED = 2;
	public static final int DEFAULT_LINEHEIGHT = 2;
	// command code
	public static final int SYS_CMD_OK = 0;
	public static final int SYS_CMD_UNKNOWNERROR = 99;
	
	public static final int SYS_CMD_LOADBEGIN = 100;
	public static final int SYS_CMD_LOADEND = 110;

	public static final int SYS_CMD_IOERROR = 1000;
	public static final int SYS_CMD_ENCODEERROR = 1010;

	public static final int SYS_CMD_REPORTLINE = 10000000;		// 行数也不会超过(1千万-1)
//	public static final int SYS_CMD_REPORTVALUE = 20000000;		// 这样的设计假设 VALUE不会超过(1千万-1) 
//	public static final int SYS_CMD_REPORTMAXVALUE = 30000000;
	
	public WalkBook application = null;

	private static Font font;

	private static int bgColor = DEFAULT_BGCOLOR;			// 背景色
	private static int fontColor = DEFAULT_FONTCOLOR;		// 字体色
	private static int scrollSpeed = DEFAULT_SCROLLSPEED;	// 卷屏速度
	private static int lineHeight = DEFAULT_LINEHEIGHT;		// 行距

	public static final Config INSTANCE = new Config();
	
	private Config() {
		font = Font.getDefaultFont();
		// load configuration stored in RMS
		load();
	}


	public Font getFont() {
		return font;
	}

	public void setFont(Font f) {
		font = f;
	}

	public void setFont(int face, int style, int size) {
		font = Font.getFont(face, style, size);
	}

	public int getBGColor() {
		return bgColor;
	}

	public void setBGColor(int c) {
		bgColor = c;
	}

	public int getFontColor() {
		return fontColor;
	}

	public void setFontColor(int c) {
		fontColor = c;
	}

	public int getScrollSpeed() {
		return scrollSpeed;
	}

	public void setScrollSpeed(int i) throws IllegalArgumentException {
		if ((i > 0) && (i < 6)) {
			scrollSpeed = i;
		} else {
			throw new IllegalArgumentException("Scroll Speed " + i);
		}
	}
	
	public int getLineHeight() {
		return lineHeight;
	}
	
	public void setLineHeight(int l) throws IllegalArgumentException {
		/*
		 * lineHeight 定义：
		 * 0 - 无行距
		 * 1 - 1像素行距
		 * 2 - 2像素行距
		 * 50 - 0.5倍字高行距
		 * 100 - 1倍字高行距
		 */
		if ((l == 0) || (l == 1) || (l == 2) || (l == 50) || (l == 100)) {
			lineHeight = l;
		} else {
			throw new IllegalArgumentException("Line Height " + l);
		}
	}

	public void load() {
		try {
			DataInputStream d = new DataInputStream(
				new ByteArrayInputStream(RMSAgent.INSTANCE.loadRecord(RMS_POS_CONF))
			);
			d.readInt();	// position for sysID
			setFontColor(d.readInt());
			setBGColor(d.readInt());
			setFont(d.readInt(), d.readInt(), d.readInt());
			setScrollSpeed(d.readInt());
			setLineHeight(d.readInt());
			d.close();
			System.out.println("成功读取设置");
		} catch (Exception e) {
			System.out.println("读取设置失败");
			System.out.println(e);
			setDefaultConfig();
		}
	}

	public void save() {
		try	{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream d = new DataOutputStream(b);
			d.writeInt(RMS_POS_CONF);
			d.writeInt(getFontColor());
			d.writeInt(getBGColor());
			d.writeInt(getFont().getFace());
			d.writeInt(getFont().getStyle());
			d.writeInt(getFont().getSize());
			d.writeInt(getScrollSpeed());
			d.writeInt(getLineHeight());
			d.close();
			RMSAgent.INSTANCE.saveRecord(RMS_POS_CONF, b.toByteArray());
			b.close();
			System.out.println("成功保存当前设置");
		} catch (Exception e) {
			// inform save failure
			System.out.println("保存设置失败");
			System.out.println(e);
		}
	}

	public void setDefaultConfig() {
		setFontColor(DEFAULT_FONTCOLOR);
		setBGColor(DEFAULT_BGCOLOR);
		setFont(Font.getDefaultFont());
		setScrollSpeed(DEFAULT_SCROLLSPEED);
		System.out.println("恢复默认设置");
	}
	
}
