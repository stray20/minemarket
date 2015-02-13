package hu.infinityhosting.minemarket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kg4b0r on 15. 02. 02..
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_SMS = "sms";
    public static final String COLOUMN_ID = "_id";
    public static final String COLOUMN_TEXT = "text";
    public static final String COLOUMN_TEL = "tel";
    public static final String COLOUMN_PRICE = "price";
    public static final String COLOUMN_DATE = "date";
    public static final String COLOUMN_STATUS = "status";
    public static final String DB_NAME = "sms.db";
    public static final int DB_VERSION = 2;

    private static final String DB_CREATE = "create table "+ TABLE_SMS +
            "("+COLOUMN_ID+" integer primary key autoincrement, "
            + COLOUMN_DATE + " text not null, "
            + COLOUMN_PRICE + " text not null, "
            + COLOUMN_TEL + " text not null, "
            + COLOUMN_TEXT + " text not null, "
            + COLOUMN_STATUS + " integer not null"
            + ");";

    public MySQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
        Log.w(MySQLiteHelper.class.getName(), "Upgrading db from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }
}
