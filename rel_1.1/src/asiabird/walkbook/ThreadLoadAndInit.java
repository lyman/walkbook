/*
 * Created on 2005-7-6
 *
 */
package asiabird.walkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.lcdui.Font;

/**
 * @author Lyman
 *
 */
public class ThreadLoadAndInit extends Thread {

	private int width = 0;
	private String fileName = null;
	private String encoding = null;
	private Book   loader = null;
	private Vector content = null;
	
	public ThreadLoadAndInit(Book book, int w, String fn) {
		loader = book;
		width = w;
		fileName = fn;
		encoding = null;
	}

	public ThreadLoadAndInit(Book book, int w, String fn, String enc) {
		loader = book;
		width = w;
		fileName = fn;
		encoding = enc;
	}
	
	public void run() {
		InputStream is = null;
		InputStreamReader isr = null;
		try {
			Config.INSTANCE.application.systemCommand(Config.SYS_CMD_LOADBEGIN);
			System.out.println("开始载入文件");
			if (fileName.equals("使用说明")) {
				is = getClass().getResourceAsStream("/doc/readme.txt");
			} else {
				is = getClass().getResourceAsStream("/book/" + fileName);
			}

			try {
				if (encoding == null) {
					isr = new InputStreamReader(is);
				} else {
					isr = new InputStreamReader(is, encoding);
				}
			} catch(UnsupportedEncodingException e) {
				Config.INSTANCE.application.systemCommand(Config.SYS_CMD_ENCODEERROR);
			}
			int c = 0;		// current char
			int cw = 0;		// current char width
			int sw = 0;		// current string width
			StringBuffer s = new StringBuffer();
			content = new Vector();
			Font font = Config.INSTANCE.getFont();
			
			long ti = System.currentTimeMillis();	// 计时开始
			while ((c = isr.read()) != -1) {
				if (c == 13) {
					// 忽略 0d 换行符
					continue;
				}
				if (c == 10) {
					// 只在 0A 时换行（DOS: 0D0A UNIX: 0A）
					content.addElement(s.toString());
					Config.INSTANCE.application.systemCommand(Config.SYS_CMD_REPORTLINE + content.size());
					s.setLength(0);
					sw = 0;
					continue;
				}
//				s.append((char)c);
				cw = font.charWidth((char)c);
/*
				if (Config.INSTANCE.getFont().stringWidth(s.toString()) > width) {
					s.deleteCharAt(s.length() - 1);
					content.addElement(s.toString());
					Config.INSTANCE.application.systemCommand(Config.SYS_CMD_REPORTLINE + content.size());
					s.setLength(0);
					s.append((char)c);
				}
*/
				if (sw + cw > width) {
					content.addElement(s.toString());
					Config.INSTANCE.application.systemCommand(Config.SYS_CMD_REPORTLINE + content.size());
					s.setLength(0);
					sw = 0;
				}
				s.append((char)c);
				sw += cw;

			}
			content.addElement(s.toString());
			ti = System.currentTimeMillis() - ti;	// 计时结束

			Config.INSTANCE.application.waitMessage("载入完成。\n总行数: " + content.size() + " , \n耗时 " + ti + "ms");
			System.out.println("载入完成。总行数: " + content.size() + " , 耗时 " + ti + "ms");
			
			// 将完成排版的内容传回调用者
			loader.setContent(content);
			Config.INSTANCE.application.systemCommand(Config.SYS_CMD_LOADEND);
			
		} catch(IOException e) {
			Config.INSTANCE.application.systemCommand(Config.SYS_CMD_IOERROR);
			System.out.println("加载文件时出错");
		} finally {
			if (isr != null) {
				try {
					isr.close();
					isr = null;
				} catch (IOException e) {
					System.out.println("关闭InputStreamReader时出错");
				}
			}
			if (is != null) {
//				try {
//					is.close();
				is = null;
//				} catch (IOException e) {
//					System.out.println("关闭InputStream时出错");
//				}
			}
		}
	}
}
