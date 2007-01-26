package asiabird.walkbook;

import javax.microedition.lcdui.Font;

public class Config {

	public int xMargin;	// 横向边距
	public int yMargin;	// 纵向边距
	public Font font;
	public int backColor;	// 背景色
	public int foreColor;	// 前景色
	public int lineMargin;	// 行距
	
	/**
	 * 默认配置
	 */
	public Config() {
		xMargin = 1;
		yMargin = 1;
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		backColor = 0x00000000;		// 黑色
		foreColor = 0x0000ff00;		// 绿色
		lineMargin = 1;
	}
	
}
