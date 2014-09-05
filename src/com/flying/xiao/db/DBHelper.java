package com.flying.xiao.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.flying.xiao.common.StringUtils;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.MyChat;
import com.flying.xiao.entity.XUserInfo;

public class DBHelper extends SQLiteOpenHelper
{
	private static final String DBNAME = "Xiao.db";
	private static final int VEWSION = 1;
	private static final String TABLENAME = "friend";
	private static final String F_COL_ID = "id";
	private static final String F_COL_USER_ID = "user_id";
	private static final String F_COL_USER_HEAD_IMAGE_URL = "user_head_image_url";
	private static final String F_COL_USER_REAL_NAME = "user_real_name";
	private static final String F_COL_USER_NAME = "user_name";
	private static final String F_COL_USER_INFO = "user_info";
	private static final String F_COL_ISONLINE = "isOnline";
	private static final String F_COL_USER_TYPE = "userTypeId";
	private static final String F_COL_USER_DETAIL = "userInfoDetail";
	private static final String F_COL_USER_PHONE = "userPhone";

	private static final String TABLENAME2 = "chatMessage";
	private static final String C_COL_ID_MAIN = "id_main";
	private static final String C_COL_FROM_USER = "from_user";
	private static final String C_COL_TO_USER = "to_user";
	private static final String C_COL_SEND_TIME = "send_time";
	private static final String C_COL_HEAD_IMAGE_URL = "head_image_url";
	private static final String C_COL_MESSAGE_INFO = "message_info";
	private static final String C_COL_IS_SEE = "is_see";
	private static final String C_COL_IS_SENDTO = "is_send_to";
	private static final String C_COL_IS_TO = "is_to";
	private static final String C_COL_IS_SEND_ERROR = "is_send_error";

	private static final String createTable = " CREATE TABLE " + TABLENAME + " (" + F_COL_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + F_COL_USER_ID + " INTEGER," + F_COL_USER_TYPE
			+ " INTEGER," + F_COL_USER_HEAD_IMAGE_URL + "  TEXT," + F_COL_USER_REAL_NAME + " TEXT,"
			+ F_COL_USER_NAME + " TEXT," + F_COL_USER_INFO + " TEXT," + F_COL_USER_DETAIL + " TEXT,"
			+ F_COL_USER_PHONE + " TEXT," + F_COL_ISONLINE + "  BOOLEAN DEFAULT false);";
	private static final String createTable1 = " CREATE TABLE " + TABLENAME2 + " (" + C_COL_ID_MAIN
			+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + C_COL_FROM_USER + " TEXT," + C_COL_TO_USER
			+ " TEXT," + C_COL_HEAD_IMAGE_URL + "  TEXT," + C_COL_MESSAGE_INFO + " TEXT," + C_COL_IS_SENDTO
			+ " BOOLEAN DEFAULT false," + C_COL_IS_TO + " BOOLEAN DEFAULT false," + C_COL_IS_SEND_ERROR
			+ " BOOLEAN DEFAULT false," + C_COL_SEND_TIME + " DATETIME ," + C_COL_IS_SEE
			+ "  BOOLEAN DEFAULT false);";

	private static DBHelper dbHelper;
	private Context context;

	public DBHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, DBNAME, factory, VEWSION);

	}

	private DBHelper(Context context)
	{
		super(context, DBNAME, null, VEWSION);
	}

	public static synchronized DBHelper getDbHelper(Context context)
	{
		if (dbHelper == null)
			dbHelper = new DBHelper(context);
		dbHelper.context = context;

		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(createTable);
		db.execSQL(createTable1);
		db.execSQL("CREATE UNIQUE INDEX unique_index_username ON " + TABLENAME + " (" + F_COL_USER_NAME
				+ ");");
	}

	public synchronized void insertFriend(XUserInfo userInfo)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(F_COL_USER_ID, userInfo.getId());
		values.put(F_COL_USER_REAL_NAME, userInfo.getUserRealName());
		values.put(F_COL_USER_NAME, userInfo.getUserName());
		values.put(F_COL_USER_HEAD_IMAGE_URL, userInfo.getUserHeadImageUrl());
		values.put(F_COL_USER_INFO, userInfo.getUserGerenshuoming());
		values.put(F_COL_USER_TYPE, userInfo.getUserTypeId());
		values.put(F_COL_USER_DETAIL, userInfo.getUserInfoDetail());
		values.put(F_COL_USER_PHONE, userInfo.getUserPhone());
		values.put(F_COL_ISONLINE, false);
		db.insert(TABLENAME, null, values);

		db.close();
	}

	public synchronized void insertFriends(List<XUserInfo> userInfos)
	{
		if (userInfos == null || userInfos.size() == 0)
		{
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		for (XUserInfo userInfo : userInfos)
		{
			// ContentValues values = new ContentValues();
			// values.put(F_COL_USER_ID, userInfo.getId());
			// values.put(F_COL_USER_REAL_NAME, userInfo.getUserRealName());
			// values.put(F_COL_USER_HEAD_IMAGE_URL,
			// userInfo.getUserHeadImageUrl());
			// values.put(F_COL_USER_INFO, userInfo.getUserGerenshuoming());
			// values.put(F_COL_USER_TYPE, userInfo.getUserTypeId());
			// values.put(F_COL_USER_DETAIL, userInfo.getUserInfoDetail());
			// values.put(F_COL_USER_PHONE, userInfo.getUserPhone());
			if (isHaveThisUser(userInfo.getUserName()))
			{ // update
				String sql = "update " + TABLENAME + " set " + F_COL_USER_ID + "=?," + F_COL_USER_REAL_NAME
						+ "=?," + F_COL_USER_HEAD_IMAGE_URL + "=?," + F_COL_USER_INFO + "=?,"
						+ F_COL_USER_TYPE + "=?," + F_COL_USER_DETAIL + "=?," + F_COL_USER_PHONE + "=?"
						+ " where " + F_COL_USER_NAME + "=?";
				db.execSQL(
						sql,
						new Object[]
						{ userInfo.getId(), userInfo.getUserRealName(), userInfo.getUserHeadImageUrl(),
								userInfo.getUserGerenshuoming(), userInfo.getUserTypeId(),
								userInfo.getUserInfoDetail(), userInfo.getUserPhone(), userInfo.getUserName() });
				// String whereClause = F_COL_USER_NAME + "=?";// 修改条件
				// String[] whereArgs =
				// { userInfo.getUserName() };// 修改条件的参数
				// db.update(TABLENAME, values, whereClause, whereArgs);// 执行修改
			} else
			{// insert
				// values.put(F_COL_USER_NAME, userInfo.getUserName());
			// db.insert(TABLENAME, null, values);
			}
		}
		db.close();
	}

	/**
	 * 从xmpp获取到的好友消息更新或者插入好友数据
	 * 
	 * @param entrieslist
	 */
	public synchronized void insertOrUpdateFriends(List<XUserInfo> userInfos)
	{
		if (userInfos == null || userInfos.size() == 0)
		{
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		for (XUserInfo user : userInfos)
		{
			String sql;
			if (isHaveThisUser(user.getUserName()))
			{ // update
				sql = "update " + TABLENAME + " set " + F_COL_ISONLINE + "=" + (user.isOnline() ? 1 : 0)
						+ " where " + F_COL_USER_NAME + "='" + user.getUserName() + "'";
			} else
			{ // insert
				sql = "INSERT INTO " + TABLENAME + " (" + F_COL_USER_NAME + ", " + F_COL_ISONLINE
						+ ") VALUES ('" + user.getUserName() + "', " + (user.isOnline() ? 1 : 0) + " )";
			}
			db.execSQL(sql);
		}
		db.close();
	}

	public List<XUserInfo> selectFriends()
	{
		List<XUserInfo> list = new ArrayList<XUserInfo>();
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME + " order by id desc";
		Cursor c = db.rawQuery(selectSql, null);
		while (c.moveToNext())
		{
			XUserInfo userInfo = new XUserInfo();
			userInfo.setId(c.getLong(c.getColumnIndex(F_COL_USER_ID)));
			userInfo.setUserRealName(c.getString(c.getColumnIndex(F_COL_USER_REAL_NAME)));
			userInfo.setUserName(c.getString(c.getColumnIndex(F_COL_USER_NAME)));
			userInfo.setUserHeadImageUrl(c.getString(c.getColumnIndex(F_COL_USER_HEAD_IMAGE_URL)));
			userInfo.setUserGerenshuoming(c.getString(c.getColumnIndex(F_COL_USER_INFO)));
			userInfo.setUserTypeId(c.getInt(c.getColumnIndex(F_COL_USER_TYPE)));
			userInfo.setUserInfoDetail(c.getString(c.getColumnIndex(F_COL_USER_DETAIL)));
			userInfo.setUserPhone(c.getString(c.getColumnIndex(F_COL_USER_PHONE)));
			int online = c.getInt(c.getColumnIndex(F_COL_ISONLINE));
			userInfo.setOnline(online == 0 ? false : true);
			list.add(userInfo);
		}
		c.close();
		db.close();
		return list;
	}

	public XUserInfo getUserInfoById(long userId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME + " where " + F_COL_USER_ID + " = " + userId;
		Cursor c = db.rawQuery(selectSql, null);
		XUserInfo userInfo = null;
		if (c.moveToNext())
		{
			userInfo = new XUserInfo();
			userInfo.setId(c.getLong(c.getColumnIndex(F_COL_USER_ID)));
			userInfo.setUserRealName(c.getString(c.getColumnIndex(F_COL_USER_REAL_NAME)));
			userInfo.setUserName(c.getString(c.getColumnIndex(F_COL_USER_NAME)));
			userInfo.setUserHeadImageUrl(c.getString(c.getColumnIndex(F_COL_USER_HEAD_IMAGE_URL)));
			userInfo.setUserGerenshuoming(c.getString(c.getColumnIndex(F_COL_USER_INFO)));
			userInfo.setUserTypeId(c.getInt(c.getColumnIndex(F_COL_USER_TYPE)));
			userInfo.setUserInfoDetail(c.getString(c.getColumnIndex(F_COL_USER_DETAIL)));
			userInfo.setUserPhone(c.getString(c.getColumnIndex(F_COL_USER_PHONE)));
			int online = c.getInt(c.getColumnIndex(F_COL_ISONLINE));
			userInfo.setOnline(online == 0 ? false : true);
		}
		c.close();
		db.close();
		return userInfo;
	}

	/**
	 * 判断表中是否存在该用户
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isHaveThisUser(long userId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME + " where " + F_COL_USER_ID + " = " + userId;
		Cursor c = db.rawQuery(selectSql, null);
		if (c.moveToNext())
		{
			return true;
		}
		return false;
	}

	public boolean isHaveThisUser(String userName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME + " where " + F_COL_USER_NAME + " = '" + userName
				+ "'";
		Cursor c = db.rawQuery(selectSql, null);
		if (c.moveToNext())
		{
			return true;
		}
		return false;
	}

	public XUserInfo getUserInfoByUserName(String userName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME + " where " + F_COL_USER_NAME + " = '" + userName
				+ "'";
		Cursor c = db.rawQuery(selectSql, null);
		XUserInfo userInfo = null;
		if (c.moveToNext())
		{
			userInfo = new XUserInfo();
			userInfo.setId(c.getLong(c.getColumnIndex(F_COL_USER_ID)));
			userInfo.setUserName(c.getString(c.getColumnIndex(F_COL_USER_NAME)));
			userInfo.setUserRealName(c.getString(c.getColumnIndex(F_COL_USER_REAL_NAME)));
			userInfo.setUserHeadImageUrl(c.getString(c.getColumnIndex(F_COL_USER_HEAD_IMAGE_URL)));
			userInfo.setUserGerenshuoming(c.getString(c.getColumnIndex(F_COL_USER_INFO)));
			userInfo.setUserTypeId(c.getInt(c.getColumnIndex(F_COL_USER_TYPE)));
			userInfo.setUserInfoDetail(c.getString(c.getColumnIndex(F_COL_USER_DETAIL)));
			userInfo.setUserPhone(c.getString(c.getColumnIndex(F_COL_USER_PHONE)));
			int online = c.getInt(c.getColumnIndex(F_COL_ISONLINE));
			userInfo.setOnline(online == 0 ? false : true);
		}
		c.close();
		db.close();
		return userInfo;
	}

	public void updateUserOnlineOrOffLine(String userName, boolean isOnline)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update " + TABLENAME + " set " + F_COL_ISONLINE + "=" + (isOnline ? 1 : 0) + " where "
				+ F_COL_USER_NAME + "='" + userName + "'";
		db.execSQL(sql);
		db.close();
	}

	public void updateUserOffLine()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update " + TABLENAME + " set " + F_COL_ISONLINE + "=0";
		db.execSQL(sql);
		db.close();
	}

	public void deleteAllFriends()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete " + TABLENAME;
		db.execSQL(sql);
		db.close();
	}

	public synchronized void insertMsg(ChatMessage msg)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(C_COL_FROM_USER, msg.getFrom());
		values.put(C_COL_TO_USER, msg.getTo());
		values.put(C_COL_MESSAGE_INFO, msg.getBody());
		values.put(C_COL_IS_TO, msg.isTo());
		values.put(C_COL_IS_SENDTO, false);
		values.put(C_COL_IS_SEE, false);
		values.put(C_COL_IS_SEND_ERROR, false);
		values.put(C_COL_SEND_TIME, StringUtils.dateToString(msg.getTime()));
		values.put(C_COL_HEAD_IMAGE_URL, msg.getUserImageHeadUrl());
		db.insert(TABLENAME2, null, values);
		db.close();
	}

	public synchronized void insertOfflineMessage(List<ChatMessage> messages)
	{

		for (ChatMessage msg : messages)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(C_COL_FROM_USER, msg.getFrom());
			values.put(C_COL_TO_USER, msg.getTo());
			values.put(C_COL_MESSAGE_INFO, msg.getBody());
			values.put(C_COL_IS_TO, false);
			values.put(C_COL_IS_SENDTO, true);
			values.put(C_COL_IS_SEE, false);
			values.put(C_COL_IS_SEND_ERROR, false);
			values.put(C_COL_SEND_TIME, StringUtils.dateToString(new Date(System.currentTimeMillis())));
			values.put(C_COL_HEAD_IMAGE_URL, msg.getUserImageHeadUrl());
			db.insert(TABLENAME2, null, values);
			db.close();
		}

	}

	// public void updateMsgSee(int messageId){
	// SQLiteDatabase db= this.getWritableDatabase();
	// String
	// sql="update "+TABLENAME2+" set "+C_COL_IS_SEE+"=1 where "+C_COL_ID+"="+messageId;
	// db.execSQL(sql);
	// db.close();
	// }

	public synchronized void updateMsgSendTo(Date time)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update " + TABLENAME2 + " set " + C_COL_IS_SENDTO + "=1 where " + C_COL_SEND_TIME
				+ "='" + StringUtils.dateToString(time) + "'";

		db.execSQL(sql);
		db.close();
	}

	public synchronized void updateMsgSendError(Date time)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update " + TABLENAME2 + " set " + C_COL_IS_SEND_ERROR + "=1 where " + C_COL_SEND_TIME
				+ "='" + StringUtils.dateToString(time) + "'";
		db.execSQL(sql);
		db.close();
	}

	public synchronized List<ChatMessage> selectMessages(String fromUser)
	{
		List<ChatMessage> list = new ArrayList<ChatMessage>();
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select * from " + TABLENAME2 + " where " + C_COL_FROM_USER + "='" + fromUser
				+ "' or " + C_COL_TO_USER + "='" + fromUser + "' order by " + C_COL_ID_MAIN;
		Cursor c = db.rawQuery(selectSql, null);
		while (c.moveToNext())
		{
			ChatMessage msg = new ChatMessage();
			msg.setBody(c.getString(c.getColumnIndex(C_COL_MESSAGE_INFO)));
			msg.setUserImageHeadUrl(c.getString(c.getColumnIndex(C_COL_HEAD_IMAGE_URL)));
			msg.setTo(c.getString(c.getColumnIndex(C_COL_TO_USER)));
			msg.setFrom(c.getString(c.getColumnIndex(C_COL_FROM_USER)));
			String time = c.getString(c.getColumnIndex(C_COL_SEND_TIME));
			msg.setTime(StringUtils.toDate(time));
			int is_see = c.getInt(c.getColumnIndex(C_COL_IS_SEE));
			int is_send_to = c.getInt(c.getColumnIndex(C_COL_IS_SENDTO));
			int is_to = c.getInt(c.getColumnIndex(C_COL_IS_TO));
			int is_send_error = c.getInt(c.getColumnIndex(C_COL_IS_SEND_ERROR));
			msg.setSee(is_see == 0 ? false : true);
			msg.setTo(is_to == 0 ? false : true);
			msg.setSendTo(is_send_to == 0 ? false : true);
			msg.setSendError(is_send_error == 0 ? false : true);
			list.add(msg);
		}
		c.close();
		db.close();
		return list;
	}

	@SuppressWarnings("resource")
	public List<MyChat> getMyChats()
	{
		List<MyChat> list = new ArrayList<MyChat>();
		SQLiteDatabase db = this.getWritableDatabase();
		String selectSql = "select " + F_COL_USER_ID + "," + F_COL_USER_HEAD_IMAGE_URL + ","
				+ F_COL_USER_NAME + "," + F_COL_USER_REAL_NAME + " from " + TABLENAME + " order by id desc";
		Cursor c = db.rawQuery(selectSql, null);

		while (c.moveToNext())
		{
			String userName = c.getString(c.getColumnIndex(F_COL_USER_NAME));
			Long userId = c.getLong(c.getColumnIndex(F_COL_USER_ID));
			String imageUrl = c.getString(c.getColumnIndex(F_COL_USER_HEAD_IMAGE_URL));
			String name = c.getString(c.getColumnIndex(F_COL_USER_REAL_NAME));
			String sql = "select " + C_COL_MESSAGE_INFO + " , " + C_COL_SEND_TIME + " from " + TABLENAME2
					+ " WHERE " + C_COL_FROM_USER + " = '" + userName + "' or " + C_COL_TO_USER + "='"
					+ userName + "' ORDER BY " + C_COL_ID_MAIN + " DESC LIMIT 1";
			Cursor cc = db.rawQuery(sql, null);
			if (cc.moveToNext())
			{
				MyChat chat = new MyChat();
				chat.setFriendUserId(userId);
				chat.setImageUrl(imageUrl);
				chat.setFriendName(name);
				chat.setFriendUserName(userName);
				chat.setLastMsg(cc.getString(cc.getColumnIndex(C_COL_MESSAGE_INFO)));
				chat.setTime(StringUtils.toDate(cc.getString(cc.getColumnIndex(C_COL_SEND_TIME))));
				// db.
				// chat.setNewMsgCount(newMsgCount);
				list.add(chat);
			}
			cc.close();
		}
		c.close();
		db.close();

		return list;
	}

	public synchronized void close()
	{
		if (dbHelper != null)
		{
			dbHelper.close();
			dbHelper = null;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// db.execSQL(createTable);
		// db.execSQL(createTable1);
	}

}
