package asiabird.walkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

public class Book {

	private String resourceName;
	private String charset;
	
 	private Config config;
	private Cache cache;
	private Vector pilot;		// 用于存放每行行首的char在文件中的位置
	private boolean inited;
	
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
		cache = new Cache(lineInScreen);
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
		// 读取RMS
		// 获得lineNum
		// 获得pilot
		
		// 否则开始排版
		inited = false;
	}
	
	private boolean composing = false;
	
	private class Composer implements Runnable {

		public void run() {
			if (!inited) {
				// 对整个文章进行排版
				int c = 0;	// 当前字符
				int cw = 0;	// 当前字符的宽度
				int cc = 0;	// 已读取字符数
				int sw = 0;
				StringBuffer s = new StringBuffer();
				try {
					while ((c = isr.read()) != -1) {
						cc++;
						if (c == 13) {
							// 忽略 0x0d 换行符
							continue;
						}
						if (c == 10) {
							// 只在 0x0a 时换行（DOS:0d0a; UNIX: 0a）
							sw = 0;
							lineNum++;
							// TODO
						}
						cw = config.font.charWidth((char)c);
						if (sw +  cw> width) {
							sw = 0;
							lineNum++;
							// TODO
						}
						s.append((char)c);
						sw += cw;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
}
