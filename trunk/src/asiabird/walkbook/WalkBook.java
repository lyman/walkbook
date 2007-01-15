/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.UnsupportedEncodingException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/**
 * @author Lyman
 *
 */
public class WalkBook extends MIDlet implements CommandListener {

	private Display display = Display.getDisplay(this);

	private CanvasWelcome Welcome  = null;
	private ListMainMenu MainMenu = new ListMainMenu();
	private CanvasBook Book = null;
	private FormOption Option = null;
	private ListBookShelf BookShelf = null;
	private CanvasWaiting Wait = null;

	private Command cmdExit = new Command("退出", Command.EXIT, 30);
	private Command cmdOK  = new Command("确定", Command.OK, 10);
//	private Command cmdCancel = new Command("取消", Command.CANCEL, 20);
	private Command cmdBack = new Command("返回", Command.BACK, 20);

	public WalkBook() {
		Config.INSTANCE.application = this;
		int mode = 0;

		try {
			byte[] b = {-28, -72, -83, -26, -106, -121, 38, 38, 69, 78, 71};
			byte[] c = new String("中文&&ENG").getBytes("UTF-8");
			mode = Config.SYS_CMD_OK;
			for (int i = 0; i < b.length; i++) {
				if (b[i]!=c[i]) {
					mode = Config.SYS_CMD_ENCODEERROR;
					break;
				}
			}
		} catch(UnsupportedEncodingException e) {
			mode = Config.SYS_CMD_ENCODEERROR;
		} catch(Exception e) {
			mode = Config.SYS_CMD_UNKNOWNERROR;
		}
		
		Welcome = new CanvasWelcome(mode);
		if (mode == Config.SYS_CMD_OK) {
			Welcome.addCommand(cmdOK);
		}
		Welcome.addCommand(cmdExit);
		Welcome.setCommandListener((CommandListener)this);
		
		// display welcome screen
		display.setCurrent(Welcome);
		// define MainMenu
		MainMenu.addCommand(cmdExit);
		MainMenu.addCommand(cmdOK);
		MainMenu.setCommandListener((CommandListener)this);
		// system property
		System.out.println(System.getProperty("microedition.locale"));
		System.out.println(System.getProperty("microedition.encoding"));
	}
	
	protected void startApp() {
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional){
		Config.INSTANCE.save();
		if (Book != null) {
			Book.save();
		}
		notifyDestroyed();
	}

	public void commandAction(Command command, Displayable screen) {
		if (command == cmdExit) {
			destroyApp(false);
			return;
		}
		
		if (screen == Welcome) {
			if (command == cmdOK) {
				display.setCurrent(MainMenu);
				Welcome = null;
				return;
			}
		}
		
		if (screen == MainMenu) {
			if (command == cmdOK) {
				switch (MainMenu.getSelectedIndex()) {
					case 0:
						showBook();
						break;
					case 1:
						showBookShelf();
						break;
					case 2: 
						showOption();
						break;
					case 3:		// reset record store
						RMSAgent.INSTANCE.reset();
						Alert reset = new Alert("WalkBook");
						reset.setType(AlertType.INFO);
						reset.setTimeout(3000);
						reset.setString("RMS '" + Config.RMSName + "' reseted.");
						display.setCurrent(reset, MainMenu);
						break;
				}
			}
		}

		if (screen == BookShelf) {
			if (command == cmdOK) {
				showBook(BookShelf.getSelectedItem());
				BookShelf = null;
				return;
			}
			if (command == cmdBack) {
				display.setCurrent(MainMenu);
				BookShelf = null;
				return;
			}
		}

		if (screen == Option) {
			if (command == cmdBack) {
				saveConfig();
				display.setCurrent(MainMenu);
				Option = null;
				return;
			}
		}

		if (screen == Book) {
			if (command == cmdBack) {
				Book.save();
				display.setCurrent(MainMenu);
				return;
			}
		}
	}

	private void showBook() {
		Book = new CanvasBook();
		Book.addCommand(cmdBack);
//		Book.addCommand(cmdExit);
		Book.setCommandListener((CommandListener)this);
		display.setCurrent(Book);
	}

	private void showBook(String fileName) {
		Book = new CanvasBook(fileName);
		Book.addCommand(cmdBack);
//		Book.addCommand(cmdExit);
		Book.setCommandListener((CommandListener)this);
//		display.setCurrent(Book);
	}
	
	private void showBookShelf() {
		BookShelf = new ListBookShelf();
		BookShelf.addCommand(cmdBack);
		BookShelf.addCommand(cmdOK);
		BookShelf.setCommandListener((CommandListener)this);
		display.setCurrent(BookShelf);
	}

	private void showOption() {
		Option = new FormOption();
		Option.addCommand(cmdBack);
//		Option.addCommand(cmdOK);
		Option.setCommandListener((CommandListener)this);
		display.setCurrent(Option);
	}

	private void saveConfig() {
		Config.INSTANCE.setFontColor(Option.getFontColor());
		Config.INSTANCE.setBGColor(Option.getBGColor());
		Config.INSTANCE.setFont(Option.getFont());
		Config.INSTANCE.setScrollSpeed(Option.getScrollSpeed());
		Config.INSTANCE.setLineHeight(Option.getLineHeight());
		Config.INSTANCE.save();
	}
	
	public void waitMessage(String msg) {
		if (Wait == null) {
			System.out.println("Wait 未创建时发生了对其的调用，msg=" + msg);
			return;
		}
		Wait.setStatus(msg);
	}
	public void systemMessage(String msg) {
		Alert alert = new Alert(msg);
		alert.setTimeout(1000);
		display.setCurrent(alert);
		
	}
	public void systemCommand(int cmdCode) {
		// 可以创建 Wait 的代码最先执行
		if (cmdCode == Config.SYS_CMD_LOADBEGIN) {
			Wait = new CanvasWaiting();
			Wait.setStatus("正在加载图书并排版");
			display.setCurrent(Wait);
			return;
		}
		// 后续的代码均要调用 Wait，若 Wait 不存在，则忽略以后代码
		if (Wait == null) {
			System.out.println("Wait 未创建时发生了对其的调用，cmdCode=" + cmdCode);
			return;
		}
		if (cmdCode == Config.SYS_CMD_LOADEND) {
			display.setCurrent(Book);
			Wait = null;
			return;
		}
		if (cmdCode == Config.SYS_CMD_IOERROR) {
			Alert alert = new Alert("加载文本时发生错误");
			alert.setTimeout(1500);
			display.setCurrent(alert, Book);
			return;
		}
		if (cmdCode == Config.SYS_CMD_ENCODEERROR) {
			Alert alert = new Alert("不支持文本的编码");
			alert.setTimeout(1500);
			display.setCurrent(alert, Book);
			return;
		}
		
		// 此处必须保证先判断最大可能的 cmdCode 才能保证逻辑正常。见 Config 类中关于 SYS_CMD 的说明
/*
		if (cmdCode > Config.SYS_CMD_REPORTMAXVALUE) {
			Wait.setMaxValue(cmdCode - Config.SYS_CMD_REPORTMAXVALUE);
			return;
		}
		if (cmdCode > Config.SYS_CMD_REPORTVALUE) {
			Wait.setValue(cmdCode - Config.SYS_CMD_REPORTVALUE);
			return;
		}
*/
		if (cmdCode > Config.SYS_CMD_REPORTLINE) {
			waitMessage("已处理 " + (cmdCode - Config.SYS_CMD_REPORTLINE) + " 行");
			return;
		}
	}
	
}
