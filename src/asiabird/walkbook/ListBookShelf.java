/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * @author Lyman
 *
 */
public class ListBookShelf extends List {

	private Image book = null;
	private Image how = null;

	public ListBookShelf() {
		super("WalkBook本地书架", Choice.IMPLICIT);

 		try	{
			book = Image.createImage("/image/book.png");
			how = Image.createImage("/image/how.png");
 		} catch (IOException e) {
			System.out.println("加载 book.png 失败");
 		}

		for (int i = 1; i < Config.MAX_BOOK + 1; i++) {
			InputStream is = null;
			try {
				is = getClass().getResourceAsStream("/book/" + i + ".txt");
				is.read();
				this.append(i + ".txt", book);
			} catch(Exception e) {
				continue;
			}
		}
		this.append("使用说明", how);
	}

	public String getSelectedItem() {
		return this.getString(this.getSelectedIndex());
	}
}
