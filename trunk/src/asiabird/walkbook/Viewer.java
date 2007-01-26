package asiabird.walkbook;

import java.io.InputStreamReader;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class Viewer extends Canvas {

	private Book book;

	private int cursor;
	private int lineNum;
	
	public Viewer(Book _book) {
		book = _book;
	}

	private class Composer implements Runnable {
		private InputStreamReader isr;
		
		public Composer(InputStreamReader _isr) {
			isr = _isr;
		}
		
		public void run() {
			
		}
	}
	
	protected void paint(Graphics g) {
		// TODO 背景
		
		// TODO 滚动条
		
		// TODO 排版提示
		
		// TODO 文本
		
	}
	
	
}
