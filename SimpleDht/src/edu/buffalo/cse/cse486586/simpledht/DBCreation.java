package edu.buffalo.cse.cse486586.simpledht;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Message;

public class DBCreation extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "ContentProvider";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "Key_Value_Pair";
	public static final String COLUMN_KEY = "key";
	public static final String COLUMN_VAL = "value";
   public static final String CREATE_TABLE="CREATE TABLE " + TABLE_NAME + "(" + COLUMN_KEY	+ " String PRIMARY KEY, " + COLUMN_VAL + " String);";
	//private static final String CREATE_TABLE="CREATE TABLE TABLE_NAME " + "(" +  " key String PRIMARY KEY, " + " value String"+ ")";
	private static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
	private static Context context;
	public DBCreation(Context context)
	{
		super(context, DATABASE_NAME, null,DATABASE_VERSION );
		this.context=context;
		//Message.message(context, "Constructer Called");

	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
     try {
    	 
		db.execSQL(CREATE_TABLE);
	//	Message.message(context, "onCreate() Called");

	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//Message.message(context, ""+e);
	}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		try {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		//	Message.message(context, "onUpgrade() Called");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
		//	Message.message(context, ""+e);
		}
	}

}
