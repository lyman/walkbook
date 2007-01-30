/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.IOException;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * @author Lyman
 *
 */
public class ListMainMenu extends List {

	private Image store = null;
//	private Image download = null;
	private Image setup = null;
	private Image read = null;
//	private Image warn = null;
	
	public ListMainMenu() {
		super("WalkBook主菜单", Choice.IMPLICIT);

 		try	{
//			download = Image.createImage("/image/download.png");
			store = Image.createImage("/image/books.png");
			setup = Image.createImage("/image/setup.png");
			read = Image.createImage("/image/book.png");
//			warn = Image.createImage("/image/warn.png");
 		} catch (IOException e) {
			System.out.println("加载 download.png/books.png/setup.png/book.png 失败");
 		}
		
		this.append("阅览中心", read);
		this.append("本地书架", store);
//		this.append("网络下载", download);
		this.append("系统设置", setup);
		if (Config.DEBUG) {
			this.append("RESET", null);
			this.append("L:" + System.getProperty("microedition.locale"), null);
			this.append("E:" + System.getProperty("microedition.encoding"), null);
		}
	}
}

