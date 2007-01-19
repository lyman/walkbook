/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * @author Lyman
 *
 */
public class CanvasBook extends Canvas implements Book {

	private boolean isInited = false;
	private int bookmark = 0;
	private Vector content = null;
	
	private int leftMargin = 1;
	private int rightMargin = 1;
	private int topMargin = 1;
	private int bottomMargin = 0;
	private int scrollbarHeight = 5;
	
	private int left = leftMargin;
	private int top = scrollbarHeight + topMargin;		// 进度条高度5像素
	private int width = getWidth() - leftMargin - rightMargin;
	private int height = getHeight() - scrollbarHeight - topMargin - bottomMargin;
	
	// lineHeight 和 lineNum 的初始值由 setEnvValue() 计算完成
	private int lineHeight = 0;
	private int lineNum = 0;
	
	private Timer timer;
	private TimerTask task;

	private boolean autoScroll = false;
	
	public class TimerTaskScroll extends TimerTask {
		public void run() {
			if (autoScroll) {
				keyPressed(getKeyCode(DOWN));
			}
		}
	}

	public CanvasBook() {
		/*
		 * WalkBook 应该自动记录了上次显示的 Book 内容和位置
		 * 因此先从 RMS 中读取保存的 Book
		 * 如果 RMS 中不存在保存记录，则使用默认
		 */
		setEnvValue();
		load();
		startScrollTimer();
	}
	
	public CanvasBook(String fileName) {
		setEnvValue();
		init(fileName);
		startScrollTimer();
	}
	
	private void setEnvValue() {
		switch (Config.INSTANCE.getLineHeight()) {
			case 0:
				lineHeight = Config.INSTANCE.getFont().getHeight();
				break;
			case 1:
				lineHeight = Config.INSTANCE.getFont().getHeight() + 1;
				break;
			case 2:
				lineHeight = Config.INSTANCE.getFont().getHeight() + 2;
				break;
			case 50:
				lineHeight = Config.INSTANCE.getFont().getHeight() + (Config.INSTANCE.getFont().getHeight() / 2);
				break;
			case 100:
				lineHeight = Config.INSTANCE.getFont().getHeight() * 2;
				break;
			default:
				lineHeight = Config.INSTANCE.getFont().getHeight();
				break;
		}
		lineNum = height / lineHeight;
	}
	
	public void save() {
		if (content == null) {
			return;
		}
		try	{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream d = new DataOutputStream(b);
			d.writeInt(Config.RMS_POS_BOOK);	// sysID
			d.writeInt(bookmark);
			d.writeInt(content.size());
			for (int i = 0; i < content.size(); i++) {
				d.writeUTF((String)content.elementAt(i));
			}
			d.close();
			RMSAgent.INSTANCE.saveRecord(Config.RMS_POS_BOOK, b.toByteArray());
			b.close();
			System.out.println("成功保存当前图书");
		} catch (Exception e) {
			// inform save failure
			Config.INSTANCE.application.systemMessage("保存当前图书失败: " + e);
			System.out.println("保存当前图书失败");
			System.out.println(e);
		}
	}
	
	public void load() {
		try {
			DataInputStream d = new DataInputStream(
				new ByteArrayInputStream(
					RMSAgent.INSTANCE.loadRecord(Config.RMS_POS_BOOK)
				)
			);
			d.readInt();				// sysID
			bookmark = d.readInt();		// bookmark
			int l = d.readInt();		// content size
			content = new Vector(l);
			for (int i = 0; i < l; i++) {
				content.addElement((String)d.readUTF());
			}
			isInited = true;
			d.close();
			System.out.println("成功读取图书");
		} catch (Exception e) {
			Config.INSTANCE.application.systemMessage("读取图书失败: " + e);
			System.out.println("读取图书失败");
			System.out.println(e);
			setDefaultBook();
		}
	}
	
	private void setDefaultBook() {
		bookmark = 0;
		init("使用说明");
		System.out.println("恢复默认图书");
	}
	
	private void startScrollTimer() {
		timer = new Timer();
		task = new TimerTaskScroll();
		timer.schedule(task, 0, Config.INSTANCE.getScrollSpeed() * 1000);
	}
	
	public void init(String fileName) {
		// 载入文本文件并进行显示格式
		isInited = false;
		Thread th = new ThreadLoadAndInit(this, width, fileName, Config.CODE);
		th.start();
	}
	
	public void setContent(Vector c) {
		content = c;
		isInited = true;
	}
	
	protected void paint(Graphics g) {
		// draw background
		g.setColor(Config.INSTANCE.getBGColor());
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw status bar
		g.setColor(0x00d0d0d0);		// gray
		g.fillRect(0, 0, getWidth(), scrollbarHeight);
		
		if (isInited) {
			// draw scroll bar
			g.setColor(0x00000000);		// black
			g.drawLine(2, 2, getWidth() - 3, 2);
			int l = lineNum * (getWidth() - 4) / content.size();
			if (l > getWidth() - 4) {
				l = getWidth() - 4;
			}
			if (l < 1) {
				l = 1;
			}
			int p = bookmark * (getWidth() - 4 - l) / content.size();
			if (p < 0) {
				p = 0;
			}
			g.fillRect(2 + p, 1, l, 3);

			// draw content
			g.setColor(Config.INSTANCE.getFontColor());
			g.setFont(Config.INSTANCE.getFont());
			for (int i = 0; i < lineNum; i++) {
				if ((bookmark + i) < content.size()) {
					g.drawString((String)content.elementAt(bookmark + i),
						left, top + i * lineHeight, Graphics.TOP | Graphics.LEFT);
				}
			}
		}
	}

	protected void keyPressed(int keyCode) {
		if (!isInited) {
			return;
		}
		switch(keyCode) {
			case KEY_NUM2:
				bookmark--;
				break;
			case KEY_NUM8:
				bookmark++;
				break;
			case KEY_NUM4:
				bookmark = bookmark - (lineNum - 1);
				break;
			case KEY_NUM6:
				bookmark = bookmark + (lineNum - 1);
				break;
/*
			case KEY_NUM1:
				bookmark = bookmark - content.size() / 10;
				break;
			case KEY_NUM3:
				bookmark = bookmark + content.size() / 10;
				break;
*/
			case KEY_NUM7:
				bookmark = bookmark - content.size() / 10;
				break;
			case KEY_NUM9:
				bookmark = bookmark + content.size() / 10;
				break;
			case KEY_STAR:
				bookmark = 0;
				break;
			case KEY_POUND:
				bookmark = content.size() - lineNum;
				break;
			case KEY_NUM5:
				autoScroll = !autoScroll;
				break;
		}
		switch(getGameAction(keyCode)) {
			case UP:
				bookmark--;
				break;
			case DOWN:
				bookmark++;
				break;
			case LEFT:
				bookmark = bookmark - (lineNum - 1);
				break;
			case RIGHT:
				bookmark = bookmark + (lineNum - 1);
				break;
/*
			case GAME_A:
				bookmark = 0;
				break;
			case GAME_B:
				bookmark = content.size() / 4;
				break;
			case GAME_C:
				bookmark = content.size() / 2;
				break;
			case GAME_D:
				bookmark = content.size() * 3 / 4;
				break;
*/				
			case FIRE:
				autoScroll = !autoScroll;
				break;
		}
		if (bookmark < 0) { bookmark = 0; }
		if (bookmark > content.size()) { bookmark = content.size(); }
		repaint();
	}

}
