package asiabird.walkbook;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class CanvasWaiting extends Canvas {

	private Timer timer;
	private TimerTask task;
	
	private int pos = 0;
	private int dir = 1;
	private static int max_pos = 100;
	private String status = "请稍候";
	
	private int h = 5;		// progress bar block height
	private int w = 10;		// progress bar block width 
	private int p = 2;		// padding between block and box
	private int bh = h + 2 * p;			// progress bar box height
	private int bw = 100 + w + 2 * p;	// progress bar box width
	private int t = getHeight() / 3;		// top of progress bar
	private int l = (getWidth() - bw) / 2;	// left of progress bar
	
	public class TimerTaskScroll extends TimerTask {
		public void run() {
			pos += dir;
			if (pos > max_pos) {
				pos = max_pos;
				dir = -1;
			};
			if (pos < 0) {
				pos = 0;
				dir = 1;
			}
			repaint();
		}
	}

	public CanvasWaiting() {
		timer = new Timer();
		task = new TimerTaskScroll();
		timer.schedule(task, 0, Config.WAITING_INTERVAL);
	}
	
	public void setStatus(String s) {
		status = s;
		repaint();
	}
	
	protected void paint(Graphics g) {
		// clear screen
		g.setColor(Config.COLOR_WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		// draw progress bar
		g.setColor(Config.COLOR_BLACK);
		g.drawRect(l, t, bw - 1, bh - 1);
		g.setColor(Config.COLOR_BLUE);
		g.fillRect(l + p + pos, t + p, w, h);
		// draw status
		g.setColor(Config.COLOR_BLACK);
		g.drawString(status, getWidth() / 2, t + bh * 2, Graphics.HCENTER | Graphics.TOP);
	}

}
