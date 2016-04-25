/**
 * 
 */
package com.dm.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**

 * 
 */
public class ContactUtil {
	// 读取联系人
	public static Uri CONTACTSURI = ContactsContract.Contacts.CONTENT_URI;// 联系人
	/**
	 * 取得联系人信息
	 * @param context
	 * @param tag
	 */
	public static void getContactsInfo(Context context, String tag) {
		String[] projections = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
		Cursor cursor = context.getContentResolver().query(CONTACTSURI, projections, null, null, null);
		int albumIndex = cursor.getColumnIndexOrThrow(projections[0]);
		int titleIndex = cursor.getColumnIndexOrThrow(projections[1]);
		while (cursor.moveToNext()) {
			String album = cursor.getString(albumIndex);
			String title = cursor.getString(titleIndex);
		}
		cursor.close();
	}

	// 根据联系人搜索联系人信息
	public static void searchContacts(Context context, String tag) {
		String searchName = "Wang";
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchName);

		// Uri uri2 =
		// Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
		// phoneNumber); 根据电话号码查找联系人

		String[] projection = new String[] { ContactsContract.Contacts._ID };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		String id = null;
		if (cursor.moveToFirst()) {
			id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
		}
		cursor.close();
		if (id != null) {
			String where = ContactsContract.Data._ID + "=" + id;
			projection = new String[] { ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
			Cursor searchcCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where, null, null);
			int nameIndex = searchcCursor.getColumnIndex(projection[0]);
			int numberIndex = searchcCursor.getColumnIndex(projection[1]);
			while (searchcCursor.moveToNext()) {
				String name = searchcCursor.getString(nameIndex);
				String number = searchcCursor.getString(numberIndex);
			}
			searchcCursor.close();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
