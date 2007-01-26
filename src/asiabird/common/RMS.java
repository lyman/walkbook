package asiabird.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordStore;

/**
 * RMS通过对RecordStore进行封装，提供了基于RecordStore的键－值存取功能。
 * @author lyman 软件工程师，稀亿网络软件（北京）有限公司，lyman@ce-lab.net
 */
public class RMS {

	private RecordStore rs;
	
	public RMS(String name) {
		try {
			rs = RecordStore.openRecordStore(name, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将键值对存入RecordStore。
	 * @param key 键
	 * @param value 值
	 * @return 该键值对存放的实际RecordID
	 * 如果键不存在，则创建这个键值对。如果键存在，则用新值更新之。
	 */
	public int setValue(String key, String value) {
		int i = getRecordID(key);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeUTF(key);
			dos.writeUTF(value);
			dos.flush();
			baos.flush();
			byte[] data = baos.toByteArray();
			rs.setRecord(i, data, 0, data.length);
			dos.close();
			dos = null;
			baos = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	/**
	 * 返回RecordStore中指定键名所对应的值
	 * @param key 键
	 * @return 值（字符串。）
	 * 如果指定的键名不存在，则创建这个键值对，并返回空字符串。
	 * 如果发生异常，则返回null。
	 */
	public String getValue(String key) {
		String result = null;
		int i = getRecordID(key);
		try {
			DataInputStream dis = getDataInputStream(i);
			dis.readUTF();	// key
			result = dis.readUTF();
			dis.close();
			dis = null;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	private DataInputStream getDataInputStream(int i) throws Exception {
		return new DataInputStream(
				new ByteArrayInputStream(rs.getRecord(i)));
	}
	
	private int getRecordID(String key) {
		int result = -1;
		try {
			DataInputStream dis;
			for (int i = 1, limit = rs.getNextRecordID(); i < limit; i++) {
				try {
					dis = getDataInputStream(i);
					if (dis.readUTF().equals(key)) {
						result = i;
						dis.close();
						dis = null;
						break;
					}
					dis.close();
					dis = null;
				} catch (Exception e) {
				}
			}
			if (result == -1) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeUTF(key);
				dos.writeUTF("");
				dos.flush();
				baos.flush();
				byte[] data = baos.toByteArray();
				result = rs.addRecord(data, 0, data.length);
				dos.close();
				dos = null;
				baos = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("getRecordID: " + key + ", " + result);
		return result;
	}
	
	/**
	 * 关闭RMS。
	 * 使用RMS的代码中应该显式调用本方法以保证资源释放。
	 */
	public void close() {
		try {
			rs.closeRecordStore();
			rs = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
