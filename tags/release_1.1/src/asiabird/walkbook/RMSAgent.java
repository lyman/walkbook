/*
 * Created on 2005-5-31
 *
 */
package asiabird.walkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * @author Lyman
 *
 */
public class RMSAgent {

	private RecordStore record;

	public static final RMSAgent INSTANCE = new RMSAgent();

	private RMSAgent() {
		if (Config.DEBUG) {
			System.out.println("清除原有的记录集");
			reset();
		}
	}

	public void reset() {
		try {
			RecordStore.deleteRecordStore(Config.RMSName);
			System.out.println("record store reset");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void open() throws RecordStoreException {
		try	{
			record = RecordStore.openRecordStore(Config.RMSName, false);
		} catch (RecordStoreNotFoundException e) {
			// create record store
			System.out.println("创建记录集");
			record = RecordStore.openRecordStore(Config.RMSName, true);

			/*
			 * 在创建记录集时就建两条空记录，
			 * 来保证ID1和ID2的record存在（因为默认设置存在ID1，当前书存在ID2）
			 * 但是这种方法并不科学，有可能会因RMS实现不同而失效
			 */
//			byte[] b = {0};
//			record.addRecord(b, 0, 0);
//			record.addRecord(b, 0, 0);
		}
	}

	private void close() throws RecordStoreException {
		record.closeRecordStore();
	}
	
	public byte[] loadRecord(int sysID) throws 
		RecordStoreException, InvalidRecordIDException, IllegalArgumentException {
		try	{
			int rID = getRecordIDFromSysID(sysID);
			open();
			return record.getRecord(rID);
		} finally {
			close();
		}
	}

	public void saveRecord(int sysID, byte[] data) throws 
		RecordStoreException, RecordStoreFullException, IllegalArgumentException,
        InvalidRecordIDException, RecordStoreException {
        try {
			int rID = getRecordIDFromSysID(sysID);
			open();
			record.setRecord(rID, data, 0, data.length);
		} finally {
			close();
		}
	}
	
	private int getRecordIDFromSysID(int sysID) throws RecordStoreException {
		int result = -1;		// -1 is not a normal value
		try {
			open();
			int sID = 0;		// sysID of current record
			int nID = 0;		// next record ID
			RecordEnumeration renum = record.enumerateRecords(null, null, false);
			try {
				renum.reset();
				while (renum.hasNextElement()) {
					nID = renum.nextRecordId();
					try {
						DataInputStream d = new DataInputStream(
								new ByteArrayInputStream(record.getRecord(nID))
							);
						sID = d.readInt();
						if (sID == sysID) {
							result = nID;
							break;
						}
					} catch (Exception e) {
						System.out.println("读取 sysID 为 " + sysID + " 的记录时出错，删除这条记录");
						System.out.println(e);
						record.deleteRecord(nID);
					}
				}
			} finally {
				renum.destroy();
			}
			// 遍历当前 record store 而没有找到 sysID，则创建新记录
			if (result == -1) {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream d = new DataOutputStream(b);
				try {
					d.writeInt(sysID);
					d.close();
					byte[] by = b.toByteArray();
					result = record.addRecord(by, 0, by.length);
				} catch (Exception e) {
					System.out.println("创建 sysID 为 " + sysID + " 的记录时出错");
					System.out.println(e);
				}
			}
		} finally {
			close();
		}
		return result;
	}
	
	public int getSpaceOccupied() {
		int i = -1;		// -1 means a illeagle value
		try {
			open();
			i = record.getSize();
			close();
		} catch (RecordStoreException e) {
			System.out.println(e);
			i = -1;
		}
		return i;
	}
	
	public int getSpaceAvailable() {
		int i = -1;		// -1 means a illeagle value
		try {
			open();
			i = record.getSizeAvailable();
			close();
		} catch (RecordStoreException e) {
			System.out.println(e);
			i = -1;
		}
		return i;
	}
}
