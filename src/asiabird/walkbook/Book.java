package asiabird.walkbook;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

public class Book {

	private String resourceName;
	private String charset;
	
 	private Config config;
	private Hashtable cache;
	private Vector pilot;
	
	private int bookmark;
	private InputStream is;
	private InputStreamReader isr;
	
	public Book(String _resourceName, String _charset) {
		resourceName = _resourceName;
		charset = _charset;
		// TODO 准备config
		config = new Config();
	}

	private int width;
	private int height;
	private int lineHeight;		// 行高
	private int lineInScreen;	// 一屏显示的行数
	private int lineNum;		// 总行数
	
	public void init(int canvasWidth, int canvasHeight) {
		width = canvasWidth - 2 * config.xMargin;
		height = canvasHeight - 2 * config.yMargin;
		lineHeight = config.font.getHeight() + config.lineMargin;
		lineInScreen = height / lineHeight + 1;
		lineNum = -1;
		
		// 准备cache和pilot
		cache = new Hashtable(lineInScreen * 4);
		pilot = new Vector(lineInScreen, lineInScreen);
		// 打开资源文件
		is = getClass().getResourceAsStream(resourceName);
		try {
			isr = new InputStreamReader(is, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 解码错误，使用平台默认解码
			isr = new InputStreamReader(is);
			charset = System.getProperty("microedition.encoding");
		}
		// 先从RMS获得
		// 获得lineNum
		// 获得pilot
		
		// 否则开始排版
		bookmark = 0;
	}
	
	private boolean composing = false;
	
	private class Composer implements Runnable {

		public void run() {
			if (lineNum < 0) {
				// 扫描整个文件
				
			}
		}
		
	}
	
}
