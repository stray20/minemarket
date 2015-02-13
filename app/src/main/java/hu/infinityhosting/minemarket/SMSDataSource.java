package hu.infinityhosting.minemarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kg4b0r on 15. 02. 02..
 */
public class SMSDataSource {
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLOUMN_ID,MySQLiteHelper.COLOUMN_DATE,MySQLiteHelper.COLOUMN_TEL,MySQLiteHelper.COLOUMN_TEXT,MySQLiteHelper.COLOUMN_PRICE,MySQLiteHelper.COLOUMN_STATUS};

    public SMSDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLiteException{
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public SMS createSMS(String date, String tel, String text, String price,long status){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLOUMN_DATE,date);
        values.put(MySQLiteHelper.COLOUMN_PRICE,price);
        values.put(MySQLiteHelper.COLOUMN_TEL,tel);
        values.put(MySQLiteHelper.COLOUMN_TEXT,text);
        values.put(MySQLiteHelper.COLOUMN_STATUS,status);

        long insertId = db.insert(MySQLiteHelper.TABLE_SMS,null,values);
        Cursor cursor = db.query(MySQLiteHelper.TABLE_SMS,allColumns,MySQLiteHelper.COLOUMN_ID + " = "+insertId,null,null,null,null);
        cursor.moveToFirst();
        SMS newSMS = cursortoSMS(cursor);
        cursor.close();

        return newSMS;
    }

    public List<SMS> getAllSMS(){
        List<SMS> smses = new ArrayList<SMS>();

        Cursor cursor = db.query(MySQLiteHelper.TABLE_SMS,allColumns,null,null,null,null,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            SMS sms = cursortoSMS(cursor);
            smses.add(sms);
            cursor.moveToNext();
        }
        cursor.close();
        return smses;
    }

    //id,date,price,tel,text

    private SMS cursortoSMS(Cursor cursor){
        SMS sms = new SMS();
        sms.setId(cursor.getLong(0));
        sms.setDate(cursor.getString(1));
        sms.setPrice(cursor.getString(2));
        sms.setTel(cursor.getString(3));
        sms.setText(cursor.getString(4));
        sms.setStatus(cursor.getLong(5));
        return sms;
    }
}
